import java.util.ArrayList;

public abstract class FindAlgorithm {

    private String distance_measure;
    protected Vertex[][] board;
    protected double distance_threshhold = 1.0;

    public FindAlgorithm(String distnace, Vertex[][] board) {
        distance_measure = distnace;
        if (distance_measure == "Chebyshev/Tchebychev")
            distance_threshhold = 0.0;
        this.board = board;
    }

    /**
     * Develop a finding algorithm that can
     * 
     * @param start  The start node
     * @param finish The end node
     * @return A boolean to determine weather or not we reached the end
     */
    public abstract boolean find(Vertex start, Vertex finish);

    public double getDistance(Vertex vertex1, Vertex vertex2) {
        // Determine what distance measure we are using and return the result from that algorithm
        switch (distance_measure) {
            case "Chebyshev/Tchebychev":
                return chebyshevDistance(vertex1, vertex2);

            case "Manhattan":
                return manhattanDistance(vertex1, vertex2);

            default:
                return euclidianDistance(vertex1, vertex2);
        }
    }

    public void setChecked(ArrayList<Vertex> neighbours) {
        // Set an arra of neighbours as checked
        for (Vertex neighbour : neighbours)
            board[neighbour.getX()][neighbour.getY()] = new Checked(neighbour.getX(), neighbour.getY());
    }

    public ArrayList<Vertex> getNeighbours(Vertex currentNode) {
        
        // Init neihgbours list
        ArrayList<Vertex> neighbours = new ArrayList<>();

        for (int x = -1; x < 2; x++)
            for (int y = -1; y < 2; y++)
                // If the neighbour is on not on the current node and not out of bounds ...
                if (!(y == 0 && x == 0) && (currentNode.getY() + y >= 0 && currentNode.getY() + y < 35
                        && currentNode.getX() + x >= 0 && currentNode.getX() + x < 35))
                        // ... If the neighbour is not a wall, the start node or the end node ...
                    if (!(board[currentNode.getX() + x][currentNode.getY() + y] instanceof Wall
                            || board[currentNode.getX() + x][currentNode.getY() + y] instanceof StartVertex
                            || board[currentNode.getX() + x][currentNode.getY() + y] instanceof EndVertex)) {
                            // ... If it's a checked node, set it's new parent to our node ...
                        if (board[currentNode.getX() + x][currentNode.getY() + y] instanceof Checked)
                            board[currentNode.getX() + x][currentNode.getY() + y].setParent(currentNode);
                        else {
                            // .. If it it isn't checked ...

                            // Init current neighbour
                            Neighbour current_neighbour = new Neighbour(currentNode.getX() + x, currentNode.getY() + y);

                            // .. If we are currently on a square where another neighbour is ...
                            if ((board[currentNode.getX() + x][currentNode.getY() + y] instanceof Neighbour)) {

                                // .. Work out the end node ...
                                Vertex start = currentNode;
                                for (int x1 = 0; x1 < 35; x1++)
                                    for (int y1 = 0; y1 < 35; y1++)
                                        if (board[x1][y1] instanceof EndVertex)
                                            start = board[x1][y1];

                                // ... If our parent node is further to the end than the previous parent then set our parent to that parent instead
                                if (euclidianDistance(currentNode, start) > euclidianDistance(
                                        board[currentNode.getX() + x][currentNode.getY() + y].getParent(), start))
                                    current_neighbour.setParent(
                                            board[currentNode.getX() + x][currentNode.getY() + y].getParent());
                                else
                                    // .. Else if we are closer than set it to our node instead
                                    current_neighbour.setParent(currentNode);

                            } else
                                // ... If we never landed on a neigbouring node (nothing is on this square) then set parent to current node
                                current_neighbour.setParent(currentNode);

                            // Set board to the current neighbour and add it to the arraylist
                            board[currentNode.getX() + x][currentNode.getY() + y] = current_neighbour;
                            neighbours.add(current_neighbour);
                        }
                    }

        return neighbours;
    }

    public void setHueristics(ArrayList<Vertex> neighbours, Vertex endNode) {
        // Set all the hueristic values of the neighbours
        for (Vertex neighbour : neighbours)
            neighbour.setDistance(getDistance(neighbour, endNode) + euclidianDistance(neighbour, neighbour.getParent()));
    }

    public double euclidianDistance(Vertex vertex1, Vertex vertex2) {
        return Math.hypot(vertex1.getX() - vertex2.getX(), vertex1.getY() - vertex2.getY());
    }

    public double chebyshevDistance(Vertex vertex1, Vertex vertex2) {
        return Math.max(Math.abs(vertex2.getY() - vertex1.getY()), Math.abs(vertex2.getX() - vertex1.getX()));
    }

    public double manhattanDistance(Vertex vertex1, Vertex vertex2) {
        return (Math.abs(vertex2.getX() - vertex1.getX()) + Math.abs(vertex2.getY() - vertex1.getY()));
    }
}
