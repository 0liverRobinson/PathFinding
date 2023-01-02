import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.BorderLayout;

public class Application implements MouseInputListener, Runnable {
    private boolean erase = false, notFound = false;
    private JFrame window;
    private Canvas c;
    private BufferStrategy buffer_strategy;
    private Graphics g;
    private int window_size = 700, tile_length = window_size/35, not_found_len = 16;
    private ArrayList<Vertex> walls;
    private Vertex[][] board = new Vertex[35][35];
    private int matrix_x, matrix_y;
    private Vertex start, finish;
    private JButton start_button, reset_button;
    private String not_found = "Can't Find Route";
    private ArrayList<FindAlgorithm> algorithms_list; 
    private JComboBox algorithm_picker, distance_picker;

    public Application()
    {
        window = new JFrame("Path Finding !");

        JPanel control_panel = new JPanel();

        algorithms_list = new ArrayList<>();

        String[] algorithms = {
            "A*",
            "Dijkstra"
        };
        String[] distances = {
            "Euclidian",
            "Chebyshev/Tchebychev",
            "Manhattan"
        };
        algorithm_picker = new JComboBox<>(algorithms);
        distance_picker = new JComboBox<>(distances);

        start_button = new JButton("Start");
        reset_button = new JButton("Reset");

        reset_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
                
            }
        });

        start_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }
            
        });


        control_panel.add(start_button);
        control_panel.add(reset_button);
        control_panel.add(new JLabel("Algorithm: "));
        control_panel.add(algorithm_picker);
        control_panel.add(new JLabel("Distance Measurement: "));
        control_panel.add(distance_picker);


        window.setSize(window_size, window_size+10);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
    


        window.setVisible(true);

        window.setLayout(new BorderLayout());



        window.add(control_panel, BorderLayout.SOUTH);

        start = new Vertex(2, 15);
        finish = new Vertex(33, 15);

        board[2][15] = new StartVertex(2, 15);

        board[32][15] = new EndVertex(32, 15);

        c = new Canvas();

        c.setVisible(true);
        c.setFocusable(false);
        c.setBackground(Color.WHITE);
        window.add(c, BorderLayout.CENTER);

        c.addMouseListener(this);
        c.addMouseMotionListener(this);
    
        c.createBufferStrategy(3);

    }


    private void drawGrid()
    {
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
            {
                g.drawLine(x * tile_length, 0, x * tile_length, window_size);
                g.drawLine(0 , y * tile_length, window_size, y * tile_length);
            }


    }

    private void drawTiles()
    {   

        // Draw all the walls
        g.setColor(Color.BLACK);
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if (board[x][y] != null)
                switch(board[x][y].getClass().getName())
                {
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


    private void resetBoard()
    {
        for (int x = 0; x < 35; x++)
            for (int y = 0; y < 35; y++)
                if ( !  ( board[x][y] instanceof StartVertex || board[x][y] instanceof EndVertex) )
                    board[x][y] = null;
        notFound = false;
    }


    private void startSearch()
    {
        FindAlgorithm findAlgorithm;
        switch(algorithm_picker.getSelectedItem().toString())
        {
            case "Dijkstra":
                findAlgorithm = new Dijkstra(distance_picker.getSelectedItem().toString(), board);
            break;
            default:
                findAlgorithm = new AStar(distance_picker.getSelectedItem().toString(), board);
            break;
        }
        notFound = !findAlgorithm.find(start, finish);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}


    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


    @Override
    public void mousePressed(MouseEvent e) {

        matrix_x = Math.round(e.getX() / tile_length); 
        matrix_y = Math.round(e.getY() / tile_length);
        erase = (e.getButton() == MouseEvent.BUTTON3);
        try {
            if ( !( board[matrix_x][matrix_y] instanceof StartVertex || board[matrix_x][matrix_y] instanceof EndVertex ) )
                board[matrix_x][matrix_y] = ( erase ) ? null : new Wall(matrix_x, matrix_y);
        } catch (Exception e2) {}
    }


    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        
        matrix_x = Math.round(e.getX() / tile_length); 
        matrix_y = Math.round(e.getY() / tile_length);

        try {
            if ( !( board[matrix_x][matrix_y] instanceof StartVertex || board[matrix_x][matrix_y] instanceof EndVertex ) )
                board[matrix_x][matrix_y] = ( erase ) ? null : new Wall(matrix_x, matrix_y);
        } catch (Exception e2) {}
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        // TODO Auto-generated method stub
        matrix_x = Math.round(e.getX() / tile_length); 
        matrix_y = Math.round(e.getY() / tile_length);

    }

    private void displayNotFound()
    {
        g.setColor(Color.RED);
        g.setFont(new Font("Times New Roman", Font.BOLD, 24));
        g.drawString(not_found, (window_size/2 - 6*not_found_len ), window_size/2 - 12);
    }

    @Override
    public void run() {
        while (true)
        { 
            buffer_strategy = c.getBufferStrategy();
            g = buffer_strategy.getDrawGraphics();

            g.clearRect(0, 0, 700, 700);

            drawGrid();

            drawTiles();

            if (notFound)
                displayNotFound();


            buffer_strategy.show();
            g.dispose();
            
            if (algorithm_picker.getSelectedItem().toString() == "Dijkstra" && distance_picker.getSelectedItem().toString() == "Chebyshev/Tchebychev")
                distance_picker.setSelectedItem("Euclidian");

            // Reduce weight of application
            try { Thread.sleep(10); } catch (InterruptedException e) {  }
        }   
    }
    public static void main(String[] args) {
        Application app = new Application();
        Thread t1 = new Thread(app);
        t1.start();
    }

}