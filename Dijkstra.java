import java.util.ArrayList;

public class Dijkstra extends FindAlgorithm {

    private boolean first_run = true;
    private Vertex start_node;

    public Dijkstra(String distnace, Vertex[][] board) {
        super(distnace, board);
    }

    @Override
    public boolean find(Vertex start, Vertex finish) {

        // Init min node and min value
        double min = -1;
        Vertex min_node = start;

        // Get our nodes neighbours
        getNeighbours(min_node);

        // If it's our first run set up our starting node
        if (first_run) {
            start_node = start;
            first_run = false;
        } else {
            // Else, set current node to a checked node 
            Checked checked = new Checked(start.getX(), start.getY());
            checked.setParent(start.getParent());
            board[start.getX()][start.getY()] = checked;
        }

        // Boolean to tell if we have found a neighbour available
        boolean hit = false;

        // Go through the board and search for a neighbour...
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (board[x][y] instanceof Neighbour) {
                    hit = true;
                    // ... When we find neigbours, determin which is the closes to the start node
                    if (getDistance(board[x][y], start_node) < min || min == -1) {
                        min = getDistance(board[x][y], start_node);
                        min_node = board[x][y];
                    }
                }
        
        // If we hit no nieghbours on the board, return false (no more nodes available so cannot find)
        if (!hit)
            return false;
        
        // If we have reached the end...
        else if (getDistance(finish, min_node) <= 1.0) {
            // ... Recontruct the path
            for (Vertex current_node = min_node; current_node.getParent() != null
                    && !(current_node instanceof StartVertex
                            || current_node instanceof EndVertex); current_node = current_node.getParent())
                board[current_node.getX()][current_node.getY()] = new PathNode(current_node.getX(),
                        current_node.getY());

            return true;
        } else
            // Recurse until we find the end or have no more spaces on the board
            return find(min_node, finish);
    }

}
