import java.util.ArrayList;

public class Dijkstra extends FindAlgorithm {

    private boolean first_run = true;
    private Vertex start_node;
    private ArrayList<Vertex> open_list = new ArrayList<>();
    public Dijkstra(String distnace, Vertex[][]board) { super(distnace, board); }

    @Override
    public boolean find(Vertex start, Vertex finish) 
    {   
        double min = -1;
        Vertex min_node = start;
        if (first_run) {
            start_node = start;
            first_run = false;
        } else{
            Checked checked = new Checked(start.getX(), start.getY());
            checked.setParent(start.getParent());
            board[start.getX()][start.getY()] = checked;
        }
        ArrayList<Vertex> neighbours = getNeighbours(start);
        
        boolean hit = false;
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (board[x][y] instanceof Neighbour)
                {
                    hit = true;
                    if (getDistance(board[x][y], start_node) < min || min == -1){
                        min = getDistance(board[x][y], start_node);
                        min_node = board[x][y];
                    }
                }
    
        if (!hit)
            return false;
        else if (getDistance(finish, min_node) <= 1.0)
        {
            // Construct path
            for (Vertex current_node = min_node; current_node.getParent() != null && ! ( current_node instanceof StartVertex || current_node instanceof EndVertex); current_node = current_node.getParent())
                board[current_node.getX()][current_node.getY()] = new PathNode(current_node.getX(), current_node.getY());
        
            return true;
        }
        else
            return find(min_node, finish);
    }
    
}
