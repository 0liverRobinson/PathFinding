public class Vertex {
    private int x,y;
    private Vertex parent;
    private double distance;
    public Vertex(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public Vertex getParent() { return parent; }
    public void setParent(Vertex parent) { this.parent = parent; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}