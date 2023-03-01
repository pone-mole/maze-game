import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import javalib.impworld.*;

import java.awt.Color;
import javalib.worldimages.*;
import java.util.Random;
import java.util.Stack;

// represent a Maze
class Maze extends World {
  // dimension of the screen and the board
  static int SCREEN_SIZE = 1000;
  static int BOARD_WIDTH;
  static int BOARD_HEIGHT;
  int vertexSize;

  // all vertices on the board
  ArrayList<Vertex> arrVertex;
  // all paths in the Maze
  ArrayList<Edge> arrEdge;
  // worklist to animate
  Stack<Vertex> worklist;
  // processed stack (with all probed Vertices)
  Stack<Vertex> processed;
  // start Vertex
  Vertex start;
  // end Vertex
  Vertex end;

  // constructor
  Maze(int boardWidth, int boardHeight) {
    Utils u = new Utils();

    Maze.BOARD_WIDTH = boardWidth;
    Maze.BOARD_HEIGHT = boardHeight;
    this.vertexSize = Maze.SCREEN_SIZE / Maze.BOARD_WIDTH;

    // set up list of Vertices in the board based on given board dimension
    this.arrVertex = u.initArrVertex(Maze.BOARD_HEIGHT, Maze.BOARD_WIDTH);

    // construct Maze
    this.arrEdge = u.constructMaze(this.arrVertex);

    // remove walls / add edges to applicable Vertices
    for (Edge e : this.arrEdge) {
      Vertex vFrom = e.from;
      Vertex vTo = e.to;

      // vFrom.addEdge(vTo);
      vFrom.addEdge(vTo);
      vTo.addEdge(vFrom);
    }

    // stores start and end Vertex
    this.start = this.arrVertex.get(0);
    this.end = this.arrVertex.get(this.arrVertex.size() - 1);

    // initializes empty worklist (for animating the path)
    this.worklist = new Stack<Vertex>();

    // initializes empty processed Stack (for coloring all probed Vertices during
    // the maze search)
    this.processed = new Stack<Vertex>();
  }

  // draw a scene in the World
  public WorldScene makeScene() {
    WorldScene scene = getEmptyScene();
    scene = this.drawMaze(scene);
    return scene;
  }

  // handles on Tick event
  public void onTick() {

    // color maze solution
    if (this.worklist.size() > 0) {
      Vertex v = this.worklist.pop();
      // creates a color based on the position of the vertex
      // with blue being for the cells close to the left side of the board
      // and red being for cells close to the right
      Color c = new Color(((225 * v.x) / BOARD_WIDTH), 0, 225 - ((225 * v.x) / BOARD_WIDTH), 225);
      v.color = c;
    }
  }

  // draw Maze
  public WorldScene drawMaze(WorldScene scene) {
    WorldImage accArrImg = new EmptyImage();

    // draw Vertices
    // draw rows
    for (int i = 0; i < Maze.BOARD_HEIGHT; i = i + 1) {
      WorldImage accRowImg = new EmptyImage();

      // draw columns
      for (int j = i * Maze.BOARD_WIDTH; j < Maze.BOARD_WIDTH * (i + 1); j = j + 1) {
        Vertex vToDraw = this.arrVertex.get(j);

        accRowImg = new BesideImage(accRowImg, vToDraw.drawVertex(this.vertexSize));
      }
      accArrImg = new AboveImage(accArrImg, accRowImg);
    }
    scene.placeImageXY(accArrImg, Maze.SCREEN_SIZE / 2, Maze.BOARD_HEIGHT * this.vertexSize / 2);

    // draw Edges
    return scene;
  }

  // handles on key event
  public void onKeyEvent(String key) {
    SearchUtils s = new SearchUtils();
    Color c = new Color(180, 227, 230);

    if (this.worklist.size() == 0) {
      // run breath first search
      if (key.equals("b")) {
        this.worklist = s.bfs(this.start, this.end, this.processed);
        System.out.println("starts breadth-first search");
        
        // colors all explored Vertices
        if (this.processed.size() > 0) {
          for (Vertex v : this.processed) {
            v.color = c;
          }
        }
        // System.out.println(this.processed);
      }

      // run depth first search
      if (key.equals("d")) {
        this.worklist = s.dfs(this.start, this.end, this.processed);
        System.out.println("starts depth-first search");
        // System.out.println(this.processed);
      }
      // colors all explored Vertices
      if (this.processed.size() > 0) {
        for (Vertex v : this.processed) {
          v.color = c;
        }
      }
    }

    // restart maze
    if (key.equals("r")) {
      this.worklist.clear();
      this.processed.clear();

      for (Vertex v : this.arrVertex) {
        v.color = Color.lightGray;
        v.drawVertex(this.vertexSize);
      }
      
      // color the starting vertex as green and the ending vertex as blue
      Vertex vStart = arrVertex.get(0);
      vStart.color = Color.green;
      Vertex vEnd = arrVertex.get(arrVertex.size() - 1);
      vEnd.color = Color.blue;

    }
  }
}

// represent a Vertex
class Vertex {
  int x;
  int y;
  Color color;
  Vertex left;
  Vertex top;
  Vertex right;
  Vertex bottom;

  // testing constructor
  Vertex(int x, int y, Color color, Vertex left, Vertex top, Vertex right, Vertex bottom) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
  }

  // convenience constructor
  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.color = Color.LIGHT_GRAY;
    this.left = null;
    this.top = null;
    this.right = null;
    this.bottom = null;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Vertex)) {
      return false;
    }
    else {
      Vertex v = (Vertex) o;
      return this.x == v.x && this.y == v.y;
    }
  }

  @Override
  public int hashCode() {
    return this.x * Maze.BOARD_WIDTH + this.y * 1000;
  }

  // draw a Vertex
  WorldImage drawVertex(int size) {
    // draw the backdrop
    WorldImage result = new RectangleImage(size, size, OutlineMode.SOLID, this.color);
    WorldImage verticalWall = new LineImage(new Posn(0, size), Color.gray);
    WorldImage horizontalWall = new LineImage(new Posn(size, 0), Color.gray);

    // draw the wall on the left
    if (this.left == null) {
      result = new BesideImage(verticalWall, result);
    }

    // draw the wall on top
    if (this.top == null) {
      result = new AboveImage(horizontalWall, result);
    }

    // draw the wall on the right
    if (this.right == null) {
      result = new BesideImage(result, verticalWall);
    }

    // draw the wall at the bottom
    if (this.bottom == null) {
      result = new AboveImage(result, horizontalWall);
    }

    return result;
  }

  // add the to Vertex (adjacent Vertex) to the corresponding orientation of that
  // to this Vertex
  void addEdge(Vertex vTo) {

    // checks if this and the given Vertex is on the same row
    if (this.x == vTo.x) {
      if (this.y < vTo.y) {
        this.bottom = vTo;
      }
      else {
        this.top = vTo;
      }
    }
    else {
      if (this.x < vTo.x) {
        this.right = vTo;
      }
      else {
        this.left = vTo;
      }
    }
  }

  // return an ArrayList of adjacent Vertices to this Vertex
  // order: left, top, right, bottom
  public ArrayList<Vertex> arrNeighbors() {
    ArrayList<Vertex> result = new ArrayList<Vertex>();

    if (this.left != null) {
      result.add(this.left);
    }
    if (this.top != null) {
      result.add(this.top);
    }
    if (this.right != null) {
      result.add(this.right);
    }
    if (this.bottom != null) {
      result.add(this.bottom);
    }

    return result;
  }
}

// represent a connection between two Vertices
class Edge {
  Vertex from;
  Vertex to;
  int weight;
  Random rand;

  // testing constructor
  Edge(Vertex from, Vertex to, int weight, Random rand) {
    this.from = from;
    this.to = to;
    this.weight = weight;
    this.rand = rand;
  }

  // convenience constructor
  Edge(Vertex from, Vertex to) {
    this.from = from;
    this.to = to;
    this.rand = new Random();
    this.weight = this.rand.nextInt(100);
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Edge)) {
      return false;
    }
    else {
      Edge e = (Edge) o;
      return this.from.equals(e.from) && this.to.equals(e.to) && this.weight == e.weight;
    }
  }

  @Override
  public int hashCode() {
    return this.from.hashCode() + this.to.hashCode() + this.weight;
  }
}

// represent Utils
class Utils {
  // creates an Arraylist to have a number of vertices according the dimension of
  // the board
  ArrayList<Vertex> initArrVertex(int boardHeight, int boardWidth) {
    ArrayList<Vertex> result = new ArrayList<Vertex>();

    // y-values
    for (int y = 0; y < boardHeight; y = y + 1) {

      // x-values
      for (int x = 0; x < boardWidth; x = x + 1) {
        Vertex v = new Vertex(x, y);
        result.add(v);
      }
    }

    // color the starting vertex as green and the ending vertex as blue
    Vertex vStart = result.get(0);
    vStart.color = Color.green;
    Vertex vEnd = result.get(result.size() - 1);
    vEnd.color = Color.blue;

    return result;
  }

  // connects the Vertices vertically and horizontally given queue of Vertices to
  // be mutated, ArrayList of pre-made Vertices and size of vertex
  PriorityQueue<Edge> allEdges(ArrayList<Vertex> arr) {
    PriorityQueue<Edge> queue = new PriorityQueue<Edge>(new EdgeComparator());
    int numRow = 1;

    // add horizontal edges
    for (int i = 0; i < arr.size() - 1; i = i + 1) {
      if (i == 0 || i % (Maze.BOARD_WIDTH * numRow - 1) != 0) {
        Edge horizontalEdge = new Edge(arr.get(i), arr.get(i + 1));
        queue.add(horizontalEdge);
      }
      else {
        numRow = numRow + 1;
      }
    }

    // add vertical edges
    for (int j = 0; j < arr.size(); j = j + 1) {
      if (j < arr.size() - Maze.BOARD_WIDTH) {
        Edge verticalEdge = new Edge(arr.get(j), arr.get(j + Maze.BOARD_WIDTH));
        queue.add(verticalEdge);
      }
    }
    return queue;
  }

  // construct Maze (minimum spanning tree) using Kruskal's Algorithm
  // provided HashMap has all Vertices represented by itself
  // return the spanning tree
  ArrayList<Edge> constructMaze(ArrayList<Vertex> arrVertex) {
    Utils u = new Utils();

    HashMap<Vertex, Vertex> representatives = new HashMap<Vertex, Vertex>();
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    PriorityQueue<Edge> worklist = u.allEdges(arrVertex);

    // initialize all Vertices to be representative of itself
    for (Vertex v : arrVertex) {
      representatives.put(v, v);
    }

    while (edgesInTree.size() < arrVertex.size() - 1) {
      Edge cheapEdge = worklist.peek();

      // check if the two ends of this edge traced back to the same tree
      if (u.find(representatives, cheapEdge.from).equals(u.find(representatives, cheapEdge.to))) {
        worklist.remove(); // discard as already connected
      }
      else {
        edgesInTree.add(cheapEdge);
        u.union(representatives, u.find(representatives, cheapEdge.from),
            u.find(representatives, cheapEdge.to));
      }
    }
    return edgesInTree;
  }

  // union the two Vertices by changing its representative in the HashMap
  // changing the value of the second Vertex's key into the representative of the
  // first Vertex
  void union(HashMap<Vertex, Vertex> map, Vertex v1, Vertex v2) {
    map.replace(v1, find(map, v2));
  }

  // find the representative of a Vertex in a HashMap
  Vertex find(HashMap<Vertex, Vertex> map, Vertex v) {
    if (v.equals(map.get(v))) {
      return v;
    }
    else {
      return find(map, map.get(v));
    }
  }
}

// represent an EdgeComparator
class EdgeComparator implements Comparator<Edge> {

  // return 1 if the first given Edge has smaller weight than the second given
  // Edge
  public int compare(Edge e1, Edge e2) {
    if (e1.weight > e2.weight) {
      return 1;
    }
    else {
      return -1;
    }
  }
}