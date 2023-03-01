import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

import javalib.worldimages.AboveImage;
import javalib.worldimages.BesideImage;
import javalib.worldimages.LineImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// represent examples and tests for Maze
class ExamplesMaze {
  // examples Vertex
  Vertex v1;
  Vertex v2;
  Vertex v3;
  Vertex v4;

  // examples Edges
  Edge e1;
  Edge e2;
  Edge e3;
  Edge e4;

  void initData() {
    // examples Vertex
    v1 = new Vertex(5, 5);
    v2 = new Vertex(5, 10);
    v3 = new Vertex(10, 5);
    v4 = new Vertex(10, 10);

    // examples Edge
    e1 = new Edge(v1, v2);
    e2 = new Edge(v2, v3);
    e3 = new Edge(v3, v4);
    e4 = new Edge(v1, v3);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // tests for class Vertex
  Vertex noWalls;
  Vertex allWalls;
  Vertex noRight;
  Vertex noLeft;
  Vertex noTop;
  Vertex noBottom;

  WorldImage noWallsExpected;
  WorldImage allWallsExpected;
  WorldImage noRightExpected;
  WorldImage noLeftExpected;
  WorldImage noTopExpected;
  WorldImage noBottomExpected;

  // set up data for testing drawVertex
  void dataForDrawVertex() {
    // a vertex with all null values fir its sides
    allWalls = new Vertex(0, 0);
    // a vertex with all vertex on its sides
    noWalls = new Vertex(2, 2);
    // vertex with a walls on 3 sides
    noRight = new Vertex(1, 2);
    noLeft = new Vertex(3, 2);
    noTop = new Vertex(2, 1);
    noBottom = new Vertex(2, 3);

    this.noWalls.top = this.noBottom;
    this.noBottom.bottom = this.noWalls;

    this.noWalls.bottom = this.noTop;
    this.noTop.top = this.noWalls;

    this.noWalls.right = this.noLeft;
    this.noLeft.left = this.noWalls;

    this.noWalls.left = this.noRight;
    this.noRight.right = this.noWalls;

    // expected images for each of these vertexes
    WorldImage verticalWallExpected = new LineImage(new Posn(0, 10), Color.gray);
    WorldImage horizontalWallExpected = new LineImage(new Posn(10, 0), Color.gray);

    allWallsExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);
    allWallsExpected = new BesideImage(verticalWallExpected, allWallsExpected);
    allWallsExpected = new AboveImage(horizontalWallExpected, allWallsExpected);
    allWallsExpected = new BesideImage(allWallsExpected, verticalWallExpected);
    allWallsExpected = new AboveImage(allWallsExpected, horizontalWallExpected);

    noWallsExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);

    noRightExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);
    noRightExpected = new BesideImage(verticalWallExpected, noRightExpected);
    noRightExpected = new AboveImage(horizontalWallExpected, noRightExpected);
    noRightExpected = new AboveImage(noRightExpected, horizontalWallExpected);

    noLeftExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);
    noLeftExpected = new AboveImage(horizontalWallExpected, noLeftExpected);
    noLeftExpected = new BesideImage(noLeftExpected, verticalWallExpected);
    noLeftExpected = new AboveImage(noLeftExpected, horizontalWallExpected);

    noTopExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);
    noTopExpected = new BesideImage(verticalWallExpected, noTopExpected);
    noTopExpected = new BesideImage(noTopExpected, verticalWallExpected);
    noTopExpected = new AboveImage(noTopExpected, horizontalWallExpected);

    noBottomExpected = new RectangleImage(10, 10, OutlineMode.SOLID, Color.LIGHT_GRAY);
    noBottomExpected = new BesideImage(verticalWallExpected, noBottomExpected);
    noBottomExpected = new AboveImage(horizontalWallExpected, noBottomExpected);
    noBottomExpected = new BesideImage(noBottomExpected, verticalWallExpected);

  }

  // test drawVertex
  void testDrawVertex(Tester t) {
    dataForDrawVertex();

    t.checkExpect(this.allWalls.drawVertex(10), this.allWallsExpected);

    t.checkExpect(this.noWalls.drawVertex(10), this.noWallsExpected);

    t.checkExpect(this.noRight.drawVertex(10), this.noRightExpected);
    t.checkExpect(this.noLeft.drawVertex(10), this.noLeftExpected);
    t.checkExpect(this.noTop.drawVertex(10), this.noTopExpected);
    t.checkExpect(this.noBottom.drawVertex(10), this.noBottomExpected);

  }

  // test addEdge
  void testAddEdge(Tester t) {

    initData();
    // checks mutating bottom field of the from Vertex
    v1.addEdge(v2);
    v2.addEdge(v1);
    t.checkExpect(v1.bottom, v2);
    t.checkExpect(v2.top, v1);

    // checks before mutation
    t.checkExpect(e3.from, v3);
    t.checkExpect(e3.to, v4);
    t.checkExpect(v3.top, null);
    t.checkExpect(v3.bottom, null);
    t.checkExpect(v4.top, null);
    t.checkExpect(v4.bottom, null);

    // add edge from the two ends of an Edge
    e3.from.addEdge(e3.to);
    e3.to.addEdge(e3.from);
    t.checkExpect(v3.bottom, v4);
    t.checkExpect(v3.top, null);
    t.checkExpect(v4.top, v3);
    t.checkExpect(v4.bottom, null);

    initData();
    // add edge horizontally
    v1.addEdge(v3);
    v3.addEdge(v1);
    t.checkExpect(v1.right, v3);
    t.checkExpect(v1.left, null);
    t.checkExpect(v3.right, null);
    t.checkExpect(v3.left, v1);
  }

  // tests for equals & hash code
  void testEqualsHashCode(Tester t) {
    Vertex vertexA = new Vertex(2, 3);
    Vertex vertexB = new Vertex(2, 3);
    Vertex vertexC = vertexA;
    Vertex vertexOther = new Vertex(1, 2);
    int otherObj = 23;

    t.checkExpect(vertexA.equals(vertexA), true);
    t.checkExpect(vertexA.equals(vertexB), true);
    t.checkExpect(vertexA.equals(vertexC), true);
    t.checkExpect(vertexA.equals(vertexOther), false);
    t.checkExpect(vertexA.equals(otherObj), false);

    t.checkExpect(vertexA.hashCode(), vertexB.hashCode());
    t.checkExpect(vertexA.hashCode(), vertexC.hashCode());
    t.checkExpect(vertexA.hashCode() == vertexOther.hashCode(), false);
  }

  // tests for arrNeighbors
  void testArrNeighbors(Tester t) {
    initData();
    Vertex testVertex = new Vertex(0, 0, Color.red, v1, v2, v3, v4);
    Vertex testVertex2 = new Vertex(0, 0, Color.red, v1, null, null, v4);

    ArrayList<Vertex> testArrVertex = new ArrayList<Vertex>(Arrays.asList(v1, v2, v3, v4));
    ArrayList<Vertex> testArrVertex2 = new ArrayList<Vertex>(Arrays.asList(v1, v4));

    t.checkExpect(testVertex.arrNeighbors(), testArrVertex);
    t.checkExpect(testVertex2.arrNeighbors(), testArrVertex2);
  }
  ////////////////////////////////////////////////////////////////////////////////
  // tests for class Edge

  // test equals 7 hascode
  void testEdgeEqualsHash(Tester t) {
    Random testRand = new Random(1);
    Vertex vertexA = new Vertex(0, 0);
    Vertex vertexB = new Vertex(1, 1);
    Edge edgeA = new Edge(vertexA, vertexB, 10, testRand);
    Edge edgeB = new Edge(vertexA, vertexB, 10, testRand);
    Edge edgeC = edgeA;
    Edge edgeOther = new Edge(vertexA, vertexA, 10, testRand);

    t.checkExpect(edgeA.equals(edgeA), true);
    t.checkExpect(edgeA.equals(edgeB), true);
    t.checkExpect(edgeA.equals(edgeC), true);
    t.checkExpect(edgeA.equals(vertexA), false);
    t.checkExpect(edgeA.equals(edgeOther), false);

    t.checkExpect(edgeA.hashCode(), edgeB.hashCode());
    t.checkExpect(edgeA.hashCode(), edgeC.hashCode());
    t.checkExpect(edgeA.hashCode() == edgeOther.hashCode(), false);

  }

  ////////////////////////////////////////////////////////////////////////////////
  // tests for class Utils

  // test initArrVertex
  void testInitArrVertex(Tester t) {
    Utils u = new Utils();
    ArrayList<Vertex> vertexArray = u.initArrVertex(60, 100); // height, width

    // array should have 6000 cells
    t.checkExpect(vertexArray.size(), 6000);

    Vertex firstVertex = vertexArray.get(0);
    Vertex rightVertex = vertexArray.get(1);
    Vertex endFirstRow = vertexArray.get(99);
    Vertex bottomVertex = vertexArray.get(100);
    Vertex lastVertex = vertexArray.get(5999);

    // check first and last cells are the correct color
    t.checkExpect(firstVertex.color, Color.green);
    t.checkExpect(lastVertex.color, Color.blue);

    // check the x, y coordinates of the first and last cells
    t.checkExpect(firstVertex.x, 0);
    t.checkExpect(firstVertex.y, 0);

    t.checkExpect(rightVertex.x, 1);
    t.checkExpect(rightVertex.y, 0);

    t.checkExpect(endFirstRow.x, 99);
    t.checkExpect(endFirstRow.y, 0);

    t.checkExpect(bottomVertex.x, 0);
    t.checkExpect(bottomVertex.y, 1);

    t.checkExpect(lastVertex.x, 99);
    t.checkExpect(lastVertex.y, 59);
  }

  ArrayList<Vertex> vertexArray;
  PriorityQueue<Edge> edgePriorityQueue;

  void initAllEdges() {
    Utils u = new Utils();
    vertexArray = u.initArrVertex(3, 3);
    edgePriorityQueue = u.allEdges(vertexArray);
  }

  // test allEdges
  /*
   * void testAllEdges(Tester t) { initAllEdges();
   * 
   * t.checkExpect(edgePriorityQueue.size(), 10);
   * 
   * // checks that the edges are sorted by weight beginning with the smallest
   * edge // first for (int i = 0; i < 9; i = i + 1) { int firstEdgeWeight =
   * edgePriorityQueue.poll().weight; int nextEdgeWeight =
   * edgePriorityQueue.peek().weight;
   * 
   * t.checkExpect(firstEdgeWeight <= nextEdgeWeight, true);
   * 
   * }
   * 
   * }
   */

  // test union
  void testUnion(Tester t) {
    Utils u = new Utils();
    Vertex vertexA = new Vertex(0, 0);
    Vertex vertexB = new Vertex(1, 0);
    Vertex vertexC = new Vertex(0, 1);
    Vertex vertexD = new Vertex(1, 1);

    HashMap<Vertex, Vertex> unionMap = new HashMap<Vertex, Vertex>();
    unionMap.put(vertexA, vertexA);
    unionMap.put(vertexB, vertexA);
    unionMap.put(vertexC, vertexD);
    unionMap.put(vertexD, vertexD);

    u.union(unionMap, vertexB, vertexC);

    t.checkExpect(unionMap.get(vertexB), vertexD);
  }

  // test find
  void testFind(Tester t) {
    Utils u = new Utils();
    Vertex vertexA = new Vertex(0, 0);
    Vertex vertexB = new Vertex(1, 0);
    Vertex vertexC = new Vertex(0, 1);
    Vertex vertexD = new Vertex(1, 1);
    Vertex vertexE = new Vertex(2, 1);

    HashMap<Vertex, Vertex> unionMap = new HashMap<Vertex, Vertex>();
    unionMap.put(vertexA, vertexA);
    unionMap.put(vertexB, vertexA);
    unionMap.put(vertexC, vertexD);
    unionMap.put(vertexD, vertexD);
    unionMap.put(vertexE, vertexC);

    t.checkExpect(u.find(unionMap, vertexA), vertexA);
    t.checkExpect(u.find(unionMap, vertexB), vertexA);
    t.checkExpect(u.find(unionMap, vertexC), vertexD);
    t.checkExpect(u.find(unionMap, vertexD), vertexD);
    t.checkExpect(u.find(unionMap, vertexE), vertexD);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // test EdgeComparator and PriorityQueue with that comparator
  void testComparatorQueue(Tester t) {
    initData();
    PriorityQueue<Edge> testQueue = new PriorityQueue<Edge>(new EdgeComparator());

    testQueue.add(e1);
    testQueue.add(e2);
    testQueue.add(e3);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // tests for interface ICollection
  // isEmpty, remove, add

  // test ICollection methods for MyQueue
  // MyQueue: first out, last in
  void testICollectionMyQueue(Tester t) {
    MyQueue<String> queue = new MyQueue<String>();
    t.checkExpect(queue.isEmpty(), true);
    queue.add("a");
    queue.add("b");
    queue.add("c");
    t.checkExpect(queue.isEmpty(), false);
    t.checkExpect(queue.remove(), "a");
    t.checkExpect(queue.isEmpty(), false);
    t.checkExpect(queue.remove(), "b");
    t.checkExpect(queue.isEmpty(), false);
    t.checkExpect(queue.remove(), "c");
    t.checkExpect(queue.isEmpty(), true);

  }

  // test ICollection methods for MyStack
  // MyStack: first out, first in
  void testICollectionMyStack(Tester t) {
    MyStack<String> stack = new MyStack<String>();
    t.checkExpect(stack.isEmpty(), true);
    stack.add("a");
    stack.add("b");
    stack.add("c");
    t.checkExpect(stack.isEmpty(), false);
    t.checkExpect(stack.remove(), "c");
    t.checkExpect(stack.isEmpty(), false);
    t.checkExpect(stack.remove(), "b");
    t.checkExpect(stack.isEmpty(), false);
    t.checkExpect(stack.remove(), "a");
    t.checkExpect(stack.isEmpty(), true);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // run the World
  void testGame(Tester t) {
    Maze maze = new Maze(100, 60);
    maze.bigBang(Maze.SCREEN_SIZE, Maze.SCREEN_SIZE / Maze.BOARD_WIDTH * Maze.BOARD_HEIGHT,
        1 / 15.0);
  }
}