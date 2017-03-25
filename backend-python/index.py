import requests
from flask import Flask, jsonify
from pprint import pprint
import heapq
import sys
import pickle
from math import radians, cos, sin, asin, sqrt

from flask import request


class Graph:
    def __init__(self):
        self.point = {}

    def add_point(self, id, edge):
        self.point[id] = edge

    def get_path(self, start, finish):
        cost = {}
        previous = {}
        nodes = []

        for point in self.point:
            # pprint(point)
            if point == start:
                cost[point] = 0
                heapq.heappush(nodes, [0, point])
            else:
                cost[point] = sys.maxsize
                heapq.heappush(nodes, [sys.maxsize, point])
            previous[point] = None

        while nodes:
            smallest = (heapq.heappop(nodes)[1])
            if smallest == finish:
                path = []
                while previous[smallest]:
                    path.append(smallest)
                    smallest = previous[smallest]
                return path
            if cost[smallest] == sys.maxsize:
                break

            for neighbor in self.point[smallest]:
                alt = cost[smallest] + self.point[smallest][neighbor]
                if alt < cost[neighbor]:
                    cost[neighbor] = alt
                    previous[neighbor] = smallest
                    for n in nodes:
                        if n[1] == neighbor:
                            n[0] = alt
                            break
                    heapq.heapify(nodes)

        return cost


class TrafficData:
    def __init__(self):
        self.id = ''
        self.name = ''
        self.type = ''
        self.text = ''
        self.longitude = 0.0
        self.latitude = 0.0

    def serialize(self):
        return {
            'id': self.id,
            'name': self.name,
            'type': self.type,
            'text': self.text,
            'longitude': self.longitude,
            'latitude': self.latitude,
        }


app = Flask(__name__)


def get_traffic_data(id):
    map_data = pickle.load(open("map_data.pickle", "rb"))
    for data in map_data:
        if data['id'] == id:
            return data
    pass


def haversine(lon1, lat1, lon2, lat2):
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * asin(sqrt(a))
    km = 6367 * c
    return km


def find_shortest(lon, lat, map_data):
    shortest_distance = sys.maxsize
    shortest_node_id = None

    for node in map_data:
        distance = haversine(float(lon), float(lat), float(node["location_y"]), float(node["location_x"]))
        if distance < shortest_distance:
            shortest_distance = distance
            shortest_node_id = node["id"]
    return shortest_node_id


@app.route('/routes')
def route():
    graph = pickle.load(open("graph.pickle", "rb"))
    map_data = pickle.load(open("map_data.pickle", "rb"))
    lon1 = request.args.get('lon1')
    lat1 = request.args.get('lat1')
    lon2 = request.args.get('lon2')
    lat2 = request.args.get('lat2')

    from_id = find_shortest(lon1, lat1, map_data)
    to_id = find_shortest(lon2, lat2, map_data)

    paths = graph.get_path(from_id, to_id)
    traffic_data = []
    routes = []
    for path in paths:
        d = get_traffic_data(path)
        traffic_data.append({'longitude': d['location_y'], 'latitude': d['location_x']})
        # routes.append({d['location_x'],d['location_y']})
    # for path in paths:
    return jsonify({"routes_data": traffic_data, "status": 200})


@app.route('/', methods=['GET', 'POST'])
def index():
    return jsonify({"Team": "Unworthy"})
    pass


@app.route('/refresh', methods=['GET', 'POST'])
def do_refresh():
    refresh()
    return jsonify({"status": 200})
    pass


def refresh():
    graph = Graph()
    r = requests.get('https://mars.aashari.id/api/get-map.json')
    request_data = r.json()
    # pprint(request_data)

    for data in request_data:
        edge = {}
        for relation in data['relation']:
            cost = relation['distance']
            for event in relation['events']:
                cost += master_cost[event['type']]
            edge[relation['id']] = cost
        graph.add_point(data['id'], edge)

    pickle.dump(graph, open("graph.pickle", "wb"))
    pickle.dump(request_data, open("map_data.pickle", "wb"))
    pass


master_cost = {'TrafficJam': 100,
               'Accident': 100,
               'DamagedRoads': 100,
               'FallenTrees': 100,
               'Floods': 100,
               'RoadClosed': 100,
               'RoadConstruction': 100
               }

if __name__ == '__main__':
    refresh()
    app.run()
