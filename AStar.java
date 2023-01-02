import java.util.ArrayList;

public class AStar extends FindAlgorithm {


    public AStar(String distnace, Vertex[][]board) { super(distnace, board); }

    @Override
    public boolean find(Vertex start, Vertex finish) {

        try {
            ArrayList<Vertex> neighbours = getNeighbours(start);
            
            if (neighbours.size() == 0 )
            {

                for (int x = 0; x < 35; x++)
                    for (int y = 0; y < 35; y++)
                        if (board[x][y] instanceof Neighbour) {
                            Checked new_node = new Checked(x, y);
                            new_node.setParent(board[x][y].getParent());
                            board[x][y] = new_node;

                            return find(new_node, finish);
                        }

                return false;

            }

            double minDistance = -1;
            Vertex bestNeighbour = start;
            
            setHueristics(neighbours, finish);

            for (Vertex neigbour : neighbours)
                if (minDistance == -1 || minDistance > neigbour.getDistance()) {
                    bestNeighbour = neigbour;
                    minDistance = neigbour.getDistance();
                }
            // Set currentNode to checked
            if ( ! ( board[start.getX()][start.getY()] instanceof StartVertex || board[start.getX()][start.getY()] instanceof EndVertex ) )
                board[start.getX()][start.getY()] = new Checked(start.getX(), start.getY());

            if (getDistance(bestNeighbour, finish) > distance_threshhold) 
                return find(bestNeighbour, finish);
            else
            {
                // Construct path
                for (Vertex current_node = start; current_node.getParent() != null && ! ( current_node instanceof StartVertex || current_node instanceof EndVertex); current_node = current_node.getParent())
                    board[current_node.getX()][current_node.getY()] = new PathNode(current_node.getX(), current_node.getY());
                
                return true;
            }
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
}
