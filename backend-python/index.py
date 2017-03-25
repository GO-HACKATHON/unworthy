from flask import Flask, jsonify
from pprint import pprint
import heapq
import sys


class Point:
    def __init__(self, id, lon, lat, edge):
        self.id = id
        self.longitude = lon
        self.latitude = lat
        self.edge = edge


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


graph = Graph()

app = Flask(__name__)


@app.route('/route/<longitude>/<latitude>')
def route(longitude, latitude):
    t = TrafficData()
    paths = graph.get_path('A', 'G')
    return jsonify({"data": t.serialize(), "routes": paths})


@app.route('/', methods=['GET', 'POST'])
def index():
    return jsonify({"Team": "Unworthy"})
    pass


if __name__ == '__main__':
    graph.add_point('A', {'B': 7, 'C': 8})
    graph.add_point('B', {'A': 7, 'F': 2})
    graph.add_point('C', {'A': 8, 'F': 6, 'G': 4})
    graph.add_point('D', {'F': 8})
    graph.add_point('E', {'H': 1})
    graph.add_point('F', {'B': 2, 'C': 6, 'D': 8, 'G': 9, 'H': 3})
    graph.add_point('G', {'C': 4, 'F': 9})
    graph.add_point('H', {'E': 1, 'F': 3})
    app.run()
