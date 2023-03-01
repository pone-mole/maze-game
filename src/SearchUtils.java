import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

// represent interface ICollection
interface ICollection<T> {
  // checks if the Collection is empty
  public boolean isEmpty();

  // return the first item and remove it from the Collection
  T remove();

  // add an item to the Collection
  void add(T item);

  // look at the first item without removing it from the Collection
  T peek();
}

// represent a MyQueue
class MyQueue<T> implements ICollection<T> {
  LinkedList<T> content;

  MyQueue() {
    this.content = new LinkedList<T>();
  }

  // check if this Queue is empty
  public boolean isEmpty() {
    return this.content.isEmpty();
  }

  // remove the first item of the Queue
  public T remove() {
    return this.content.remove();
  }

  // add an item to the tail of the Queue
  public void add(T item) {
    this.content.addLast(item);
  }

  // look at the first item without removing it from the Collection
  public T peek() {
    return this.content.peekFirst();
  }
}

// represent a MyStack 
class MyStack<T> implements ICollection<T> {
  Stack<T> content;

  // constructor
  MyStack() {
    this.content = new Stack<T>();
  }

  // checks if this Stack is empty
  public boolean isEmpty() {
    return this.content.isEmpty();
  }

  // removes the first item in the Stack
  public T remove() {
    return this.content.pop();
  }

  // add an item to the top of the Stack
  public void add(T item) {
    this.content.push(item);
  }

  // look at the first item without removing it from the Collection
  public T peek() {
    return this.content.peek();
  }
}

// represent a SearchUtils
class SearchUtils {
  // proceeds breadth-first search
  public Stack<Vertex> bfs(Vertex from, Vertex to, Stack<Vertex> processed) {
    return searchHelp(from, to, new MyQueue<Vertex>(), processed);
  }

  // proceeds depth-first search
  public Stack<Vertex> dfs(Vertex from, Vertex to, Stack<Vertex> processed) {
    return searchHelp(from, to, new MyStack<Vertex>(), processed);
  }

  // search a Graph breadth-first or depth-first depends on the inputed worklist
  Stack<Vertex> searchHelp(Vertex start, Vertex end, ICollection<Vertex> worklist,
      Stack<Vertex> processed) {
    Stack<Vertex> result = new Stack<Vertex>();
    Stack<Vertex> test = new Stack<Vertex>();
    HashMap<Vertex, Edge> cameFromEdge = new HashMap<Vertex, Edge>();
    boolean notEnd = true;

    // initializes the search by adding the starting Vertex to the worklist
    worklist.add(start);

    while (!worklist.isEmpty() && notEnd) {
      Vertex next = worklist.peek();

      // check if this Vertex is previously processed
      if (processed.contains(next)) {
        worklist.remove();
      }
      else {
        // add this Vertex to the processed list (alreadySeen)
        processed.push(next);

        // process path if have found the answer path
        if (next.equals(end)) {
          result = reconstruct(cameFromEdge, next, start);
          notEnd = false;
        }
        else {
          // process its neighbor(s)
          ArrayList<Vertex> neighbors = next.arrNeighbors();
          for (Vertex v : neighbors) {
            if (!processed.contains(v)) {
              worklist.add(v);
              test.add(v);
              cameFromEdge.put(v, new Edge(next, v));
            }
          }
        }
      }
    }
    return result;
  }
  /*
   * HashMap<String, Edge> cameFromEdge;
   * 
   * ???<Node> worklist; // A Queue or a Stack, depending on the algorithm
   * 
   * 
   * 
   * initialize the worklist to contain the starting node
   * 
   * While(the worklist is not empty)
   * 
   * Node next = the next item from the worklist
   * 
   * If (next has already been processed)
   * 
   * discard it
   * 
   * Else If (next is the target):
   * 
   * return reconstruct(cameFromEdge, next);
   * 
   * Else:
   * 
   * For each neighbor n of next:
   * 
   * Add n to the worklist
   * 
   * Record the edge (next->n) in the cameFromEdge map
   */

  // reconstruct the path it takes to go from the start to the end
  // initially given end Vertex as key
  Stack<Vertex> reconstruct(HashMap<Vertex, Edge> cameFromEdge, Vertex key, Vertex start) {
    Stack<Vertex> vertexPath = new Stack<Vertex>();
    int n = 0;
    System.out.println(cameFromEdge.values());

    while (vertexPath.size() < cameFromEdge.size() && !key.equals(start)) {
      System.out.println(n++);

      Edge e = cameFromEdge.get(key);
      Vertex from = e.from;

      vertexPath.add(key);
      key = from;
    }

    vertexPath.add(start);
    System.out.println(vertexPath);
    return vertexPath;
  }
}