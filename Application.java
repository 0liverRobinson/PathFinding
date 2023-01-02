import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.BorderLayout;

public class Application implements MouseInputListener, Runnable {

    private boolean erase = false, notFound = false;
    private JFrame window;
    private Canvas c;
    private BufferStrategy buffer_strategy;
    private Graphics g;
    private int window_size = 700, tile_length = window_size / 35, not_found_len = 16;
    private Vertex[][] board = new Vertex[35][35];
    private int matrix_x, matrix_y;
    private Vertex start, finish;
    private JButton start_button, reset_button;
    private String not_found = "Can't Find Route";
    private JComboBox algorithm_picker, distance_picker;

    public Application() {

        // Create new window
        window = new JFrame("Path Finding !");
        
        // Init bottom GUI
        JPanel control_panel = new JPanel();
        
        // Init Combo box selection values
        String[] algorithms = {
                "A*",
                "Dijkstra"
        };
        String[] distances = {
                "Euclidian",
                "Chebyshev/Tchebychev",
                "Manhattan"
        };
        
        // Init combo boxes
        algorithm_picker = new JComboBox<>(algorithms);
        distance_picker = new JComboBox<>(distances);
        
        // Init buttons
        start_button = new JButton("Start");
        reset_button = new JButton("Reset");

        // Set reset button to reset onclick
        reset_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();

            }
        });

        // Set start button to start onclick
        start_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }

        });

        // Add all bottom GUI elements
        control_panel.add(start_button);
        control_panel.add(reset_button);
        control_panel.add(new JLabel("Algorithm: "));
        control_panel.add(algorithm_picker);
        control_panel.add(new JLabel("Distance Measurement: "));
        control_panel.add(distance_picker);

        // Set window attributes
        window.setSize(window_size, window_size + 10);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        // Set mouselisteners
        window.addMouseListener(this);
        window.addMouseMotionListener(this);

        // Show window on screen
        window.setVisible(true);

        // Set layout
        window.setLayout(new BorderLayout());

        // Add bottom GUI 
        window.add(control_panel, BorderLayout.SOUTH);

        // Init start and end nodes
        start = new Vertex(2, 15);
        finish = new Vertex(33, 15);
        board[2][15] = new StartVertex(2, 15);
        board[32][15] = new EndVertex(32, 15);

        // Init canvas
        c = new Canvas();
        c.setVisible(true);
        c.setFocusable(false);
        c.setBackground(Color.WHITE);

        // Add canvas to window
        window.add(c, BorderLayout.CENTER);

        // Add mouselisteners for canvas
        c.addMouseListener(this);
        c.addMouseMotionListener(this);

        // Set canvas buffer strategy for drawing
        c.createBufferStrategy(3);

    }

    private void drawGrid() {

        // Draw 35/35 grid in light grey 
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++) {
                // Draw Vertical line
                g.drawLine(x * tile_length, 0, x * tile_length, window_size);
                // Draw horizontal line
                g.drawLine(0, y * tile_length, window_size, y * tile_length);
            }
    }

    private void drawTiles() {

        // Draw all the walls
        g.setColor(Color.BLACK);
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (board[x][y] != null)
                    switch (board[x][y].getClass().getName()) {
                        case "Wall":
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            break;

                        case "StartVertex":
                            g.setColor(Color.GREEN);
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            g.setColor(Color.BLACK);
                            break;

                        case "Checked":
                            g.setColor(Color.CYAN);
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            g.setColor(Color.BLACK);
                            break;

                        case "Neighbour":
                            g.setColor(Color.GRAY);
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            g.setColor(Color.BLACK);
                            break;

                        case "EndVertex":
                            g.setColor(Color.RED);
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            g.setColor(Color.BLACK);
                            break;

                        case "PathNode":
                            g.setColor(Color.BLUE);
                            g.fillRect(x * tile_length, y * tile_length, tile_length, tile_length);
                            g.setColor(Color.BLACK);
                            break;
                    }

    }

    private void resetBoard() {
        // Delete every item on the board apart from the start and end nodes
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (!(board[x][y] instanceof StartVertex || board[x][y] instanceof EndVertex))
                    board[x][y] = null;
        notFound = false;
    }

    private void startSearch() {
        FindAlgorithm findAlgorithm;

        // First get rid of any previous parts
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (board[x][y] instanceof PathNode || board[x][y] instanceof Checked || board[x][y] instanceof Neighbour)
                    board[x][y] = null;

        // Init the find algorithm based on combobox selection
        switch (algorithm_picker.getSelectedItem().toString()) {
            case "Dijkstra":
                findAlgorithm = new Dijkstra(distance_picker.getSelectedItem().toString(), board);
                break;
            default:
                findAlgorithm = new AStar(distance_picker.getSelectedItem().toString(), board);
                break;
        }
        
        // Start search and get result on weather we found the node or not
        notFound = !findAlgorithm.find(start, finish);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Get mouse position in terms of x,y on the grid
        matrix_x = Math.round(e.getX() / tile_length);
        matrix_y = Math.round(e.getY() / tile_length);
        
        // If right clicking start erasing
        erase = (e.getButton() == MouseEvent.BUTTON3);
        try {
            // Draw / erase on the board but not on the end or start vertexes
            if (!(board[matrix_x][matrix_y] instanceof StartVertex || board[matrix_x][matrix_y] instanceof EndVertex))
                board[matrix_x][matrix_y] = (erase) ? null : new Wall(matrix_x, matrix_y);
        } catch (Exception e2) {
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Get mouse position in terms of x,y on the grid
        matrix_x = Math.round(e.getX() / tile_length);
        matrix_y = Math.round(e.getY() / tile_length);

        try {
            // Draw / erase on the board but not on the end or start vertexes
            if (!(board[matrix_x][matrix_y] instanceof StartVertex || board[matrix_x][matrix_y] instanceof EndVertex))
                board[matrix_x][matrix_y] = (erase) ? null : new Wall(matrix_x, matrix_y);
        } catch (Exception e2) {
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Get mouse position in terms of x,y on the grid
        matrix_x = Math.round(e.getX() / tile_length);
        matrix_y = Math.round(e.getY() / tile_length);

    }

    private void displayNotFound() {
        // Display text in centre of screen "Can't Find Route"
        g.setColor(Color.RED);
        g.setFont(new Font("Times New Roman", Font.BOLD, 24));
        g.drawString(not_found, (window_size / 2 - 6 * not_found_len), window_size / 2 - 12);
    }

    @Override
    public void run() {
        while (true) {
            // Init buffer strategies 
            buffer_strategy = c.getBufferStrategy();
            g = buffer_strategy.getDrawGraphics();

            // Clear board
            g.clearRect(0, 0, 700, 700);
            
            // Draw board contents
            drawGrid();
            drawTiles();

            // Display not found msg if not found
            if (notFound)
                displayNotFound();

            // Show drawing
            buffer_strategy.show();
            g.dispose();

            // If we are choosing chebyshev and dijkstra, set to euclid (chebyshev dijkstra combo doesn't work)
            if (algorithm_picker.getSelectedItem().toString() == "Dijkstra"
                    && distance_picker.getSelectedItem().toString() == "Chebyshev/Tchebychev")
                distance_picker.setSelectedItem("Euclidian");

            // Reduce weight of application
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    public static void main(String[] args) {
        Application app = new Application();
        Thread t1 = new Thread(app);
        t1.start();
    }

}