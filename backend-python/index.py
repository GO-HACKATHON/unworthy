import requests
from flask import Flask, jsonify
from pprint import pprint
import heapq
import sys
import pickle


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


@app.route('/routes/<longitude>/<latitude>')
def route(longitude, latitude):
    graph = pickle.load(open("graph.pickle", "rb"))
    paths = graph.get_path(longitude, latitude)
    traffic_data = []
    routes = []
    for path in paths:
        d = get_traffic_data(path)
        traffic_data.append(d)
        # routes.append({d['location_x'],d['location_y']})
    # for path in paths:
    return jsonify({"routes_data": traffic_data})


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
