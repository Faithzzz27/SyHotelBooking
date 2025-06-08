/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package faith.syhotelbooking;

/**
 *
 * @author ADMIN
 */
/*
 * Professional Hotel Booking System with SQLite Database
 * Enhanced UI and Database Integration
 */


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Vector;

public class SyHotelBookingSystem {
    private static CardLayout layout = new CardLayout();
    private static JPanel mainPanel = new JPanel(layout);
    private static Connection connection;
    private static String currentUser = null;
    
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            initializeDatabase();
            
            JFrame frame = new JFrame("SY Hotel Booking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);
            frame.setIconImage(createHotelIcon());

            mainPanel.setBackground(BACKGROUND_COLOR);
            mainPanel.add(createLoginPanel(), "Login");
            mainPanel.add(createRegisterPanel(), "Register");
            mainPanel.add(createDashboardPanel(), "Dashboard");
            mainPanel.add(createReservationPanel(), "Reservation");
            mainPanel.add(createViewReservationsPanel(), "ViewReservations");


            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }

    private static void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:hotel_booking.db");
            
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    full_name TEXT,
                    email TEXT,
                    phone TEXT,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            // Create reservations table
            String createReservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    guest_name TEXT NOT NULL,
                    room_type TEXT NOT NULL,
                    check_in_date DATE NOT NULL,
                    check_out_date DATE NOT NULL,
                    num_guests INTEGER NOT NULL,
                    total_amount DECIMAL(10,2),
                    status TEXT DEFAULT 'Active',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            """;
            
            Statement stmt = connection.createStatement();
            stmt.execute(createUsersTable);
            stmt.execute(createReservationsTable);
            stmt.close();
            
            System.out.println("Database initialized successfully!");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Image createHotelIcon() {
        // Create a simple hotel icon
        return new ImageIcon().getImage();
    }

    private static JPanel createLoginPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 120));
        
        JLabel titleLabel = new JLabel("SY Hotel Booking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(40, 60, 40, 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel loginTitle = new JLabel("Login to Your Account", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loginTitle.setForeground(TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(loginTitle, gbc);
        
        gbc.gridwidth = 1;
        
        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        JButton loginBtn = createStyledButton("Login", PRIMARY_COLOR);
        JButton registerBtn = createStyledButton("Create Account", SECONDARY_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(loginBtn, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 10, 10, 10);
        formPanel.add(registerBtn, gbc);
        
        loginBtn.addActionListener(e -> handleLogin(usernameField.getText(), new String(passwordField.getPassword())));
        registerBtn.addActionListener(e -> layout.show(SyHotelBookingSystem.mainPanel, "Register"));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static JPanel createRegisterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(SUCCESS_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 60, 30, 60));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField usernameField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JTextField fullNameField = createStyledTextField();
        JTextField emailField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        
        String[] labels = {"Username:", "Password:", "Full Name:", "Email:", "Phone:"};
        JComponent[] fields = {usernameField, passwordField, fullNameField, emailField, phoneField};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
        
        JButton registerBtn = createStyledButton("Register", SUCCESS_COLOR);
        JButton backBtn = createStyledButton("Back to Login", SECONDARY_COLOR);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(registerBtn, gbc);
        
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 10, 10, 10);
        formPanel.add(backBtn, gbc);
        
        registerBtn.addActionListener(e -> handleRegistration(
            usernameField.getText(), new String(passwordField.getPassword()),
            fullNameField.getText(), emailField.getText(), phoneField.getText()
        ));
        
        backBtn.addActionListener(e -> layout.show(SyHotelBookingSystem.mainPanel, "Login"));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static JPanel createDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 100));
        
        JLabel welcomeLabel = new JLabel("Welcome to SY Hotel", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutBtn = createStyledButton("Logout", ACCENT_COLOR);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            layout.show(SyHotelBookingSystem.mainPanel, "Login");
        });
        
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        
        // Menu Panel
        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        menuPanel.setBackground(BACKGROUND_COLOR);
        
        JButton newReservationBtn = createMenuButton("New Reservation", "Make a new hotel reservation");
        JButton viewReservationsBtn = createMenuButton("View Reservations", "View and manage your reservations");
        JButton profileBtn = createMenuButton("Profile", "Update your profile information");
        JButton helpBtn = createMenuButton("Help & Support", "Get help and contact support");
        
        newReservationBtn.addActionListener(e -> layout.show(SyHotelBookingSystem.mainPanel, "Reservation"));
        viewReservationsBtn.addActionListener(e -> {
            refreshReservationsTable();
            layout.show(SyHotelBookingSystem.mainPanel, "ViewReservations");
        });
        
        menuPanel.add(newReservationBtn);
        menuPanel.add(viewReservationsBtn);
        menuPanel.add(profileBtn);
        menuPanel.add(helpBtn);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static JPanel createReservationPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Make a Reservation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JButton backBtn = createStyledButton("← Dashboard", Color.WHITE);
        backBtn.setForeground(PRIMARY_COLOR);
        backBtn.addActionListener(e -> layout.show(SyHotelBookingSystem.mainPanel, "Dashboard"));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backBtn, BorderLayout.WEST);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField guestNameField = createStyledTextField();
        JComboBox<String> roomTypeBox = new JComboBox<>(new String[]{"Standard Single",
                                                                     "Standard Double",
                                                                     "Deluxe Suite",
                                                                     "Presidential Suite"});
        roomTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField checkInField = createStyledTextField();
        JTextField checkOutField = createStyledTextField();
        JSpinner numGuestsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        numGuestsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        checkInField.setText(today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        checkOutField.setText(tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        String[] labels = {"Guest Name:", "Room Type:", "Check-in Date:", "Check-out Date:", "Number of Guests:"};
        JComponent[] fields = {guestNameField, roomTypeBox, checkInField, checkOutField, numGuestsSpinner};
        
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }
        
        JButton reserveBtn = createStyledButton("Make Reservation", SUCCESS_COLOR);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 10, 10, 10);
        formPanel.add(reserveBtn, gbc);
        
        reserveBtn.addActionListener(e -> handleReservation(
            guestNameField.getText(),
            (String) roomTypeBox.getSelectedItem(),
            checkInField.getText(),
            checkOutField.getText(),
            (Integer) numGuestsSpinner.getValue()
        ));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static JPanel createViewReservationsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JLabel titleLabel = new JLabel("Your Reservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        
        JButton backBtn = createStyledButton("← Dashboard", Color.WHITE);
        backBtn.setForeground(PRIMARY_COLOR);
        backBtn.addActionListener(e -> layout.show(SyHotelBookingSystem.mainPanel, "Dashboard"));
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(backBtn, BorderLayout.WEST);
        
        // Table Panel
        String[] columnNames = {"ID", "Guest Name", "Room Type", "Check-in", "Check-out", "Guests", "Amount", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable reservationsTable = new JTable(tableModel);
        reservationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reservationsTable.setRowHeight(25);
        reservationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        reservationsTable.getTableHeader().setBackground(SECONDARY_COLOR);
        reservationsTable.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(reservationsTable);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }

    private static JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private static JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        // Override UI delegate colors
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private static JButton createMenuButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(127, 140, 141));
        
        button.add(titleLabel, BorderLayout.NORTH);
        button.add(descLabel, BorderLayout.CENTER);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(248, 249, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }

    private static void handleLogin(String username, String password) {
        if (username.trim().isEmpty() || password.isEmpty()) {
            showErrorMessage("Please fill in all fields.");
            return;
        }

        try {
            String sql = "SELECT id, full_name FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                currentUser = username;
                showSuccessMessage("Login successful! Welcome " + (rs.getString("full_name") != null ? rs.getString("full_name") : username));
                layout.show(SyHotelBookingSystem.mainPanel, "Dashboard");
            } else {
                showErrorMessage("Invalid username or password.");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            showErrorMessage("Login failed: " + e.getMessage());
        }
    }

    private static void handleRegistration(String username, String password, String fullName, String email, String phone) {
        if (username.trim().isEmpty() || password.isEmpty()) {
            showErrorMessage("Username and password are required.");
            return;
        }

        try {
            String sql = "INSERT INTO users (username, password, full_name, email, phone) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName.isEmpty() ? null : fullName);
            pstmt.setString(4, email.isEmpty() ? null : email);
            pstmt.setString(5, phone.isEmpty() ? null : phone);
            
            pstmt.executeUpdate();
            pstmt.close();
            
            showSuccessMessage("Registration successful! Please login.");
            layout.show(SyHotelBookingSystem.mainPanel, "Login");
            
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                showErrorMessage("Username already exists. Please choose a different username.");
            } else {
                showErrorMessage("Registration failed: " + e.getMessage());
            }
        }
    }

    private static void handleReservation(String guestName, String roomType, String checkIn, String checkOut, int numGuests) {
        if (guestName.trim().isEmpty() || checkIn.trim().isEmpty() || checkOut.trim().isEmpty()) {
            showErrorMessage("Please fill in all required fields.");
            return;
        }

        try {
            LocalDate checkInDate = LocalDate.parse(checkIn);
            LocalDate checkOutDate = LocalDate.parse(checkOut);
            
            if (checkInDate.isBefore(LocalDate.now())) {
                showErrorMessage("Check-in date cannot be in the past.");
                return;
            }
            
            if (!checkOutDate.isAfter(checkInDate)) {
                showErrorMessage("Check-out date must be after check-in date.");
                return;
            }
            
            double dailyRate = switch (roomType.split(" - ")[0]) {
                case "Standard Single" -> 99.0;
                case "Standard Double" -> 149.0;
                case "Deluxe Suite" -> 299.0;
                case "Presidential Suite" -> 599.0;
                default -> 99.0;
            };
            
            long days = checkInDate.until(checkOutDate).getDays();
            double totalAmount = dailyRate * days;
            
            // Get user ID
            String getUserIdSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement getUserStmt = connection.prepareStatement(getUserIdSql);
            getUserStmt.setString(1, currentUser);
            ResultSet userRs = getUserStmt.executeQuery();
            
            if (userRs.next()) {
                int userId = userRs.getInt("id");
                
                // Insert reservation
                String sql = "INSERT INTO reservations (user_id, guest_name, room_type, check_in_date, check_out_date, num_guests, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, userId);
                pstmt.setString(2, guestName);
                pstmt.setString(3, roomType);
                pstmt.setString(4, checkIn);
                pstmt.setString(5, checkOut);
                pstmt.setInt(6, numGuests);
                pstmt.setDouble(7, totalAmount);
                
                pstmt.executeUpdate();
                pstmt.close();
                
                String message = String.format(
                    "Reservation successful!\n\n" +
                    "Guest: %s\n" +
                    "Room: %s\n" +
                    "Dates: %s to %s (%d nights)\n" +
                    "Guests: %d\n" +
                    "Total Amount: $%.2f\n\n" +
                    "Thank you for choosing SY Hotel!",
                    guestName, roomType, checkIn, checkOut, days, numGuests, totalAmount
                );
                
                showSuccessMessage(message);
                layout.show(SyHotelBookingSystem.mainPanel, "Dashboard");
            }
            
            userRs.close();
            getUserStmt.close();
            
        } catch (DateTimeParseException e) {
            showErrorMessage("Please enter valid dates in YYYY-MM-DD format.");
        } catch (SQLException e) {
            showErrorMessage("Reservation failed: " + e.getMessage());
        }
    }

    private static void refreshReservationsTable() {
        try {
            String getUserIdSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement getUserStmt = connection.prepareStatement(getUserIdSql);
            getUserStmt.setString(1, currentUser);
            ResultSet userRs = getUserStmt.executeQuery();
            
            if (userRs.next()) {
                int userId = userRs.getInt("id");
                
                String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY created_at DESC";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                
                // Find the table model in the ViewReservations panel
                Component[] components = ((JPanel) mainPanel.getComponent(4)).getComponents();
                JScrollPane scrollPane = (JScrollPane) components[1];
                JTable table = (JTable) scrollPane.getViewport().getView();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                
                model.setRowCount(0); // Clear existing data
                
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getInt("id"));
                    row.add(rs.getString("guest_name"));
                    row.add(rs.getString("room_type"));
                    row.add(rs.getString("check_in_date"));
                    row.add(rs.getString("check_out_date"));
                    row.add(rs.getInt("num_guests"));
                    row.add(String.format("$%.2f", rs.getDouble("total_amount")));
                    row.add(rs.getString("status"));
                    model.addRow(row);
                }
                
                rs.close();
                pstmt.close();
            }
            
            userRs.close();
            getUserStmt.close();
            
        } catch (SQLException e) {
            showErrorMessage("Failed to load reservations: " + e.getMessage());
        }
    }

    private static void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(mainPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(mainPanel, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
