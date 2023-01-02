import java.util.ArrayList;

public class AStar extends FindAlgorithm {

    public AStar(String distnace, Vertex[][] board) {
        super(distnace, board);
    }

    @Override
    public boolean find(Vertex start, Vertex finish) {

        try {

            // Get the current nodes neighbours
            ArrayList<Vertex> neighbours = getNeighbours(start);

            // If we get no neighbours ...
            if (neighbours.size() == 0) {

                // ... Check the entire board for any open nodes available and start again from them
                for (int x = 0; x < 35; x++)
                    for (int y = 0; y < 35; y++)
                        if (board[x][y] instanceof Neighbour) {
                            Checked new_node = new Checked(x, y);
                            new_node.setParent(board[x][y].getParent());
                            board[x][y] = new_node;

                            return find(new_node, finish);
                        }
                
                // If there was not a single node available return false (we cannot finish the path)
                return false;

            }

            // Init distance and best neighbour vars
            double minDistance = -1;
            Vertex bestNeighbour = start;

            // Set all the neighbours hueristic values
            setHueristics(neighbours, finish);

            // Find the neighbour with the lowest hueristic value (the best node)
            for (Vertex neigbour : neighbours)
                if (minDistance == -1 || minDistance > neigbour.getDistance()) {
                    bestNeighbour = neigbour;
                    minDistance = neigbour.getDistance();
                }

            // Set currentNode to checked if we are not on the start or end vertex
            if (!(board[start.getX()][start.getY()] instanceof StartVertex
                    || board[start.getX()][start.getY()] instanceof EndVertex))
                board[start.getX()][start.getY()] = new Checked(start.getX(), start.getY());
            
            // If we are not at the end, recurse again
            if (getDistance(bestNeighbour, finish) > distance_threshhold)
                return find(bestNeighbour, finish);
            else {

                // Reconstruct path using parent nodes
                for (Vertex current_node = start; current_node.getParent() != null
                        && !(current_node instanceof StartVertex
                                || current_node instanceof EndVertex); current_node = current_node.getParent())
                    board[current_node.getX()][current_node.getY()] = new PathNode(current_node.getX(),
                            current_node.getY());

                return true;
            }
        } catch (Exception e) {
            // If we get an error, return false (not found)
            e.printStackTrace();
            return false;
        }
    }
}
