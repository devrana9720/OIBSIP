package com.oibsip;

import com.oibsip.reservation.dao.ReservationDAO;
import com.oibsip.reservation.dao.UserDAO;
import com.oibsip.reservation.model.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class App {

    private static final Color BG = new Color(245, 247, 251);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color DARK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    private static final Font NORMAL =
            new Font("Segoe UI", Font.PLAIN, 13);

    private static final Font BOLD =
            new Font("Segoe UI", Font.BOLD, 13);

    private static final String[] CITIES = {
        "Agra", "Ahmedabad", "Amritsar", "Bengaluru",
        "Bhopal", "Chandigarh", "Chennai", "Dehradun",
        "Delhi", "Gurugram", "Hyderabad", "Jaipur",
        "Jalandhar", "Kanpur", "Kolkata", "Lucknow",
        "Ludhiana", "Mumbai", "Noida", "Patna",
        "Prayagraj", "Pune", "Rajpura", "Saharanpur",
        "Shimla", "Surat", "Varanasi"
    };

    private static final Map<String, String> TRAINS = Map.of(
        "12001", "Bhopal Shatabdi (Sample)",
        "12002", "New Delhi Shatabdi (Sample)",
        "12005", "Kalka Shatabdi (Sample)",
        "12006", "Kalka Shatabdi Return (Sample)",
        "12425", "Jammu Rajdhani (Sample)",
        "12426", "Jammu Rajdhani Return (Sample)",
        "12951", "Mumbai Rajdhani (Sample)",
        "12952", "Mumbai Rajdhani Return (Sample)",
        "12017", "Dehradun Shatabdi (Sample)",
        "12018", "Dehradun Shatabdi Return (Sample)"
    );

    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::showWelcome);
    }

    // ---------------- WELCOME SCREEN ----------------

    private static void showWelcome() {
        JFrame frame = new JFrame("RailPlan | Welcome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 440);
        frame.setMinimumSize(new Dimension(560, 400));
        frame.setLocationRelativeTo(null);

        JPanel root = new ImageBackgroundPanel();
        root.setLayout(new BorderLayout(0, 20));
        root.setBorder(new EmptyBorder(35, 45, 35, 45));

        JLabel brand = new JLabel("RailPlan");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 42));
        brand.setForeground(Color.WHITE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Your journey starts here");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(Color.WHITE);
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Search sample trains and manage your reservations.");
        subtitle.setFont(NORMAL);
        subtitle.setForeground(Color.WHITE);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.add(Box.createVerticalGlue());
        center.add(brand);
        center.add(Box.createVerticalStrut(18));
        center.add(heading);
        center.add(Box.createVerticalStrut(8));
        center.add(subtitle);
        center.add(Box.createVerticalGlue());

        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 14, 0));
        buttons.setOpaque(false);

        JButton login = primaryButton("Login");
        JButton signup = secondaryButton("Create Account");

        login.addActionListener(e -> {
            frame.dispose();
            showLogin();
        });

        signup.addActionListener(e -> {
            frame.dispose();
            showSignup();
        });

        buttons.add(login);
        buttons.add(signup);
        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ---------------- LOGIN SCREEN ----------------

    private static void showLogin() {
        JFrame frame = new JFrame("RailPlan | Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(470, 430);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel root = new ImageBackgroundPanel();
        root.setLayout(new BorderLayout(0, 18));
        root.setBorder(new EmptyBorder(25, 32, 25, 32));

        JLabel heading = new JLabel("Welcome back");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Sign in to manage your train reservations");
        subtitle.setFont(NORMAL);
        subtitle.setForeground(Color.WHITE);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(new BoxLayout(
                headingPanel, BoxLayout.Y_AXIS));
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        root.add(headingPanel, BorderLayout.NORTH);

        JPanel form = card();
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setLayout(new GridBagLayout());

        JTextField username = styledTextField("");
        JPasswordField password = new JPasswordField();
        stylePasswordField(password);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(form, gbc, 0, "Username", username);
        addFormRow(form, gbc, 1, "Password", password);

        JButton login = primaryButton("Sign In");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        form.add(login, gbc);

        JButton signup = secondaryButton("Create an account");
        gbc.gridy = 3;
        form.add(signup, gbc);

        login.addActionListener(e -> {
            String user = username.getText().trim();
            String pass = new String(password.getPassword());

            if (user.isBlank() || pass.isBlank()) {
                message(frame,
                        "Enter both username and password.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean authenticated =
                        new UserDAO().authenticate(user, pass);

                if (authenticated) {
                    frame.dispose();
                    createHomeScreen();
                } else {
                    message(frame,
                            "Incorrect username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                    password.setText("");
                }
            } catch (SQLException | IllegalStateException ex) {
                message(frame,
                        "Could not log in:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        signup.addActionListener(e -> {
            frame.dispose();
            showSignup();
        });

        password.addActionListener(e -> login.doClick());

        root.add(form, BorderLayout.CENTER);

        JButton back = secondaryButton("Back to Welcome");
        back.addActionListener(e -> {
            frame.dispose();
            showWelcome();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        footer.add(back);
        root.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ---------------- SIGNUP SCREEN ----------------

    private static void showSignup() {
        JFrame frame = new JFrame("RailPlan | Create Account");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(510, 570);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel root = new ImageBackgroundPanel();
        root.setLayout(new BorderLayout(0, 15));
        root.setBorder(new EmptyBorder(22, 30, 22, 30));

        JLabel heading = new JLabel("Create your account");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 27));
        heading.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel(
                "Create an account to manage your train journeys");
        subtitle.setFont(NORMAL);
        subtitle.setForeground(Color.WHITE);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(new BoxLayout(
                headingPanel, BoxLayout.Y_AXIS));
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        root.add(headingPanel, BorderLayout.NORTH);

        JPanel form = card();
        form.setBorder(new EmptyBorder(15, 18, 15, 18));
        form.setLayout(new GridBagLayout());

        JTextField fullName = styledTextField("");
        JTextField username = styledTextField("");
        JTextField email = styledTextField("");

        JPasswordField password = new JPasswordField();
        JPasswordField confirmPassword = new JPasswordField();

        stylePasswordField(password);
        stylePasswordField(confirmPassword);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(form, gbc, 0, "Full name", fullName);
        addFormRow(form, gbc, 1, "Username", username);
        addFormRow(form, gbc, 2, "Email (optional)", email);
        addFormRow(form, gbc, 3, "Password (8+ characters)", password);
        addFormRow(form, gbc, 4, "Confirm password", confirmPassword);

        JButton register = primaryButton("Create Account");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        form.add(register, gbc);

        root.add(form, BorderLayout.CENTER);

        JButton backToLogin =
                secondaryButton("Already registered? Login");

        backToLogin.addActionListener(e -> {
            frame.dispose();
            showLogin();
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setOpaque(false);
        footer.add(backToLogin);
        root.add(footer, BorderLayout.SOUTH);

        register.addActionListener(e -> {
            String name = fullName.getText().trim();
            String user = username.getText().trim();
            String mail = email.getText().trim();
            String pass = new String(password.getPassword());
            String confirm = new String(confirmPassword.getPassword());

            if (name.isBlank() || user.isBlank()
                    || pass.isBlank() || confirm.isBlank()) {
                message(frame,
                        "Fill in all required fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (user.length() < 3) {
                message(frame,
                        "Username must contain at least 3 characters.",
                        "Invalid Username",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!mail.isBlank()
                    && !mail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                message(frame,
                        "Enter a valid email address or leave it blank.",
                        "Invalid Email",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (pass.length() < 8) {
                message(frame,
                        "Password must contain at least 8 characters.",
                        "Weak Password",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!pass.equals(confirm)) {
                message(frame,
                        "Passwords do not match.",
                        "Password Mismatch",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                boolean registered =
                        new UserDAO().register(name, user, mail, pass);

                if (registered) {
                    message(frame,
                            "Account created successfully!\n"
                                    + "You can now log in.",
                            "Registration Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                    frame.dispose();
                    showLogin();
                } else {
                    message(frame,
                            "Account could not be created. Please try again.",
                            "Registration Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException | IllegalStateException ex) {
                message(frame,
                        "Registration failed. The username or email "
                                + "may already exist, or the database "
                                + "may be unavailable.\n\nDetails: "
                                + ex.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.setContentPane(root);
        frame.setVisible(true);
    }

        // ---------------- HOME DASHBOARD ----------------

    private static void createHomeScreen() {
        JFrame frame = new JFrame("RailPlan | Train Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1120, 720);
        frame.setMinimumSize(new Dimension(900, 620));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel brand = new JLabel("RailPlan");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 30));
        brand.setForeground(BLUE);
        nav.add(brand, BorderLayout.WEST);

        JPanel navActions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0));
        navActions.setOpaque(false);

        JLabel tag = new JLabel("TRAIN RESERVATION SYSTEM");
        tag.setFont(BOLD);
        tag.setForeground(BLUE);
        tag.setOpaque(true);
        tag.setBackground(new Color(239, 246, 255));
        tag.setBorder(new EmptyBorder(10, 12, 10, 12));

        JButton logout = secondaryButton("Logout");
        logout.addActionListener(e -> {
            frame.dispose();
            showWelcome();
        });

        navActions.add(tag);
        navActions.add(logout);
        nav.add(navActions, BorderLayout.EAST);
        root.add(nav, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(24, 28, 18, 28));

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Plan your train journey");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(DARK);

        JLabel subtitle = new JLabel(
                "Search sample trains, book a reservation, "
                        + "and manage it using a PNR.");
        subtitle.setFont(NORMAL);
        subtitle.setForeground(MUTED);

        intro.add(heading);
        intro.add(Box.createVerticalStrut(5));
        intro.add(subtitle);

        // Search card
        JPanel searchCard = card();
        searchCard.setLayout(new BorderLayout(0, 18));
        searchCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel searchTitle = new JLabel("Search your journey");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchTitle.setForeground(DARK);
        searchCard.add(searchTitle, BorderLayout.NORTH);

        JComboBox<String> from = cityCombo();
        JComboBox<String> to = cityCombo();

        JButton swap = secondaryButton("↔");
        swap.setFont(new Font("Segoe UI Symbol", Font.BOLD, 24));
        swap.setForeground(BLUE);
        swap.setPreferredSize(new Dimension(60, 42));
        swap.setToolTipText("Swap source and destination");

        swap.addActionListener(e -> {
            String oldFrom = comboText(from);
            String oldTo = comboText(to);

            from.getEditor().setItem(oldTo);
            to.getEditor().setItem(oldFrom);
        });

        JPanel routeFields = new JPanel(new GridLayout(1, 3, 8, 0));
        routeFields.setOpaque(false);
        routeFields.add(field("FROM", from));
        routeFields.add(field("SWAP", swap));
        routeFields.add(field("TO", to));

        JTextField date = styledTextField(
                LocalDate.now().plusDays(7).toString());

        JButton calendar = secondaryButton("Calendar");
        calendar.addActionListener(e -> chooseDate(frame, date));

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.add(date, BorderLayout.CENTER);
        datePanel.add(calendar, BorderLayout.EAST);

        JComboBox<String> travelClass = styledCombo(new String[]{
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });

        JPanel otherFields = new JPanel(new GridLayout(1, 2, 12, 0));
        otherFields.setOpaque(false);
        otherFields.add(field("JOURNEY DATE", datePanel));
        otherFields.add(field("TRAVEL CLASS", travelClass));

        JPanel fields = new JPanel(new BorderLayout(0, 14));
        fields.setOpaque(false);
        fields.add(routeFields, BorderLayout.NORTH);
        fields.add(otherFields, BorderLayout.CENTER);

        JButton search = primaryButton("Search Trains");
        search.addActionListener(e -> {
            String source = comboText(from);
            String destination = comboText(to);
            LocalDate journeyDate = parseJourneyDate(frame, date);

            if (journeyDate == null) {
                return;
            }

            if (!isKnownCity(source) || !isKnownCity(destination)) {
                message(frame,
                        "Select a city from the suggestions for both fields.",
                        "Invalid City",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (source.equalsIgnoreCase(destination)) {
                message(frame,
                        "Source and destination must be different.",
                        "Invalid Route",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            showSampleResults(
                    frame,
                    source,
                    destination,
                    journeyDate,
                    (String) travelClass.getSelectedItem());
        });

        JPanel searchBottom = new JPanel(new BorderLayout());
        searchBottom.setOpaque(false);

        JLabel hint = new JLabel(
                "Sample train data only — no real tickets or payments.");
        hint.setFont(NORMAL);
        hint.setForeground(MUTED);

        searchBottom.add(hint, BorderLayout.WEST);
        searchBottom.add(search, BorderLayout.EAST);

        JPanel searchCenter = new JPanel(new BorderLayout(0, 18));
        searchCenter.setOpaque(false);
        searchCenter.add(fields, BorderLayout.CENTER);
        searchCenter.add(searchBottom, BorderLayout.SOUTH);

        searchCard.add(searchCenter, BorderLayout.CENTER);

        // Reservation actions
        JPanel actions = card();
        actions.setLayout(new BorderLayout(0, 15));
        actions.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel actionsTitle = new JLabel("Manage reservations");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsTitle.setForeground(DARK);
        actions.add(actionsTitle, BorderLayout.NORTH);

        JPanel actionList = new JPanel();
        actionList.setOpaque(false);
        actionList.setLayout(new BoxLayout(
                actionList, BoxLayout.Y_AXIS));

        JButton book = actionButton(
                "Book a reservation",
                "Enter passenger and journey details");

        JButton view = actionButton(
                "View reservations",
                "Review reservations and PNRs saved in MySQL");

        JButton cancel = actionButton(
                "Cancel ticket by PNR",
                "Find a booking and cancel it using its PNR");

        book.addActionListener(e ->
                openBookingForm(
                        frame, "", "Sleeper", "", "", "", ""));

        view.addActionListener(e -> showReservations(frame));
        cancel.addActionListener(e -> openCancellationScreen(frame));

        actionList.add(book);
        actionList.add(Box.createVerticalStrut(10));
        actionList.add(view);
        actionList.add(Box.createVerticalStrut(10));
        actionList.add(cancel);

        actions.add(actionList, BorderLayout.CENTER);

        // Information card
        JPanel info = card();
        info.setLayout(new BorderLayout(0, 18));
        info.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel infoTitle = new JLabel("Reservation features");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setForeground(DARK);
        info.add(infoTitle, BorderLayout.NORTH);

        JPanel infoRows = new JPanel();
        infoRows.setOpaque(false);
        infoRows.setLayout(new BoxLayout(
                infoRows, BoxLayout.Y_AXIS));

        infoRows.add(infoRow(
                "Unique PNR",
                "Use the generated PNR to find a booking."));
        infoRows.add(Box.createVerticalStrut(18));

        infoRows.add(infoRow(
                "Booking confirmation",
                "Review saved passenger and train details."));
        infoRows.add(Box.createVerticalStrut(18));

        infoRows.add(infoRow(
                "Safe simulation",
                "Sample trains only; no genuine tickets or payments."));

        info.add(infoRows, BorderLayout.CENTER);

        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0));
        lower.setOpaque(false);
        lower.add(actions);
        lower.add(info);

        JPanel middle = new JPanel(new BorderLayout(0, 18));
        middle.setOpaque(false);
        middle.add(searchCard, BorderLayout.NORTH);
        middle.add(lower, BorderLayout.CENTER);

        content.add(intro, BorderLayout.NORTH);
        content.add(middle, BorderLayout.CENTER);

        JScrollPane contentScroll = new JScrollPane(content);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScroll.getVerticalScrollBar().setUnitIncrement(16);
        contentScroll.getViewport().setBackground(BG);

        root.add(contentScroll, BorderLayout.CENTER);

        JLabel footer = new JLabel("RailPlan | OIBSIP Java Development | Train-only simulator"+ " | Developed by Dev Rana");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(MUTED);
        footer.setBorder(new EmptyBorder(0, 28, 14, 28));
        root.add(footer, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static boolean isKnownCity(String city) {
        for (String known : CITIES) {
            if (known.equalsIgnoreCase(city.trim())) {
                return true;
            }
        }
        return false;
    }

    // ---------------- SAMPLE TRAIN SEARCH RESULTS ----------------

    private static void showSampleResults(
            JFrame parent,
            String from,
            String to,
            LocalDate date,
            String travelClass) {

        JDialog dialog = new JDialog(
                parent, "Train Search Results", true);

        dialog.setSize(850, 600);
        dialog.setMinimumSize(new Dimension(750, 520));
        dialog.setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("Available sample trains");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
                from + " → " + to + " | " + date + " | " + travelClass);
        subtitle.setFont(NORMAL);
        subtitle.setForeground(MUTED);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(
                heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        root.add(heading, BorderLayout.NORTH);

        JPanel results = new JPanel();
        results.setOpaque(false);
        results.setLayout(new BoxLayout(
                results, BoxLayout.Y_AXIS));

        String[][] samples = {
            {"12001", "Bhopal Shatabdi (Sample)",
                    "06:15", "12:00", "5h 45m", "450"},
            {"12005", "Kalka Shatabdi (Sample)",
                    "10:30", "16:45", "6h 15m", "620"},
            {"12425", "Jammu Rajdhani (Sample)",
                    "17:45", "23:10", "5h 25m", "780"},
            {"12017", "Dehradun Shatabdi (Sample)",
                    "13:10", "19:20", "6h 10m", "550"},
            {"12951", "Mumbai Rajdhani (Sample)",
                    "08:00", "14:30", "6h 30m", "490"}
        };

        for (String[] train : samples) {
            JPanel route = card();
            route.setLayout(new BorderLayout(12, 8));
            route.setBorder(new EmptyBorder(15, 14, 15, 14));

            JPanel details = new JPanel();
            details.setOpaque(false);
            details.setLayout(new BoxLayout(
                    details, BoxLayout.Y_AXIS));

            JLabel name = new JLabel(
                    train[1] + " (" + train[0] + ")");
            name.setFont(BOLD);
            name.setForeground(DARK);

            JLabel times = new JLabel(
                    train[2] + "  ─────────  " + train[3]);
            times.setFont(new Font("Segoe UI", Font.BOLD, 18));
            times.setForeground(DARK);

            JLabel routeText = new JLabel(
                    from + " → " + to + " | " + train[4]
                            + " | " + travelClass);
            routeText.setFont(NORMAL);
            routeText.setForeground(MUTED);

            details.add(name);
            details.add(Box.createVerticalStrut(7));
            details.add(times);
            details.add(Box.createVerticalStrut(5));
            details.add(routeText);

            JPanel action = new JPanel();
            action.setOpaque(false);
            action.setLayout(new BoxLayout(
                    action, BoxLayout.Y_AXIS));

            JLabel fare = new JLabel("₹" + train[5] + " / person");
            fare.setFont(new Font("Segoe UI", Font.BOLD, 18));
            fare.setForeground(new Color(234, 88, 12));

            JButton choose = primaryButton("Book");
            choose.addActionListener(e -> {
                dialog.dispose();
                openBookingForm(
                        parent,
                        date.toString(),
                        travelClass,
                        from,
                        to,
                        train[0],
                        train[1]);
            });

            action.add(fare);
            action.add(Box.createVerticalStrut(8));
            action.add(choose);

            route.add(details, BorderLayout.CENTER);
            route.add(action, BorderLayout.EAST);
            route.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

            results.add(route);
            results.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(results);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        root.add(scroll, BorderLayout.CENTER);

        JButton close = secondaryButton("Close");
        close.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        footer.add(close);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

        // ---------------- BOOKING FORM ----------------

    private static void openBookingForm(
            JFrame parent,
            String date,
            String travelClass,
            String source,
            String destination,
            String selectedTrainNumber,
            String selectedTrainName) {

        JFrame frame = new JFrame("RailPlan | Book Train Reservation");
        frame.setSize(650, 650);
        frame.setMinimumSize(new Dimension(570, 590));
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel title = new JLabel("Book a Train Reservation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(DARK);
        root.add(title, BorderLayout.NORTH);

        JPanel form = card();
        form.setBorder(new EmptyBorder(18, 20, 18, 20));
        form.setLayout(new GridBagLayout());

        JTextField name = styledTextField("");
        JTextField trainNumber = styledTextField(selectedTrainNumber);
        JTextField trainName = styledTextField(selectedTrainName);

        trainName.setEditable(false);
        trainName.setBackground(new Color(248, 250, 252));

        JComboBox<String> from = cityCombo();
        JComboBox<String> to = cityCombo();

        from.getEditor().setItem(source);
        to.getEditor().setItem(destination);

        String initialDate = date.isBlank()
                ? LocalDate.now().plusDays(7).toString()
                : date;

        JTextField journey = styledTextField(initialDate);

        JButton calendar = secondaryButton("Calendar");
        calendar.addActionListener(e -> chooseDate(frame, journey));

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.add(journey, BorderLayout.CENTER);
        datePanel.add(calendar, BorderLayout.EAST);

        JComboBox<String> classes = styledCombo(new String[]{
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });
        classes.setSelectedItem(travelClass);

        trainNumber.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {

            private void updateTrainName() {
                String value = trainNumber.getText().trim();
                trainName.setText(TRAINS.getOrDefault(value, ""));
            }

            @Override
            public void insertUpdate(
                    javax.swing.event.DocumentEvent e) {
                updateTrainName();
            }

            @Override
            public void removeUpdate(
                    javax.swing.event.DocumentEvent e) {
                updateTrainName();
            }

            @Override
            public void changedUpdate(
                    javax.swing.event.DocumentEvent e) {
                updateTrainName();
            }
        });

        trainName.setText(
                TRAINS.getOrDefault(selectedTrainNumber, ""));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 5, 7, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(form, gbc, 0, "Passenger name", name);
        addFormRow(form, gbc, 1, "Train number", trainNumber);
        addFormRow(form, gbc, 2, "Train name (auto-filled)", trainName);
        addFormRow(form, gbc, 3, "Source city", from);
        addFormRow(form, gbc, 4, "Destination city", to);
        addFormRow(form, gbc, 5, "Journey date", datePanel);
        addFormRow(form, gbc, 6, "Travel class", classes);

        root.add(form, BorderLayout.CENTER);

        JButton clear = secondaryButton("Clear");
        JButton save = primaryButton("Confirm Booking");

        clear.addActionListener(e -> {
            name.setText("");
            trainNumber.setText("");
            trainName.setText("");
            from.getEditor().setItem("");
            to.getEditor().setItem("");
            journey.setText(LocalDate.now().plusDays(7).toString());
            classes.setSelectedIndex(0);
        });

        save.addActionListener(e -> {
            String passenger = name.getText().trim();
            String number = trainNumber.getText().trim();
            String train = trainName.getText().trim();
            String sourceStation = comboText(from);
            String destinationStation = comboText(to);
            String dateText = journey.getText().trim();

            if (passenger.isBlank()
                    || number.isBlank()
                    || sourceStation.isBlank()
                    || destinationStation.isBlank()
                    || dateText.isBlank()) {
                message(frame,
                        "Please fill in all required fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!number.matches("\\d+")) {
                message(frame,
                        "Train number must contain digits only.",
                        "Invalid Train Number",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (train.isBlank()
                    || !TRAINS.containsKey(number)) {
                message(frame,
                        "Choose a supported sample train number.",
                        "Unknown Train",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (sourceStation.equalsIgnoreCase(destinationStation)) {
                message(frame,
                        "Source and destination must differ.",
                        "Invalid Route",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isKnownCity(sourceStation)
                    || !isKnownCity(destinationStation)) {
                message(frame,
                        "Select both cities from the suggestions.",
                        "Invalid City",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate journeyDate;

            try {
                journeyDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                message(frame,
                        "Use YYYY-MM-DD for the journey date.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (journeyDate.isBefore(LocalDate.now())) {
                message(frame,
                        "Journey date cannot be in the past.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Reservation reservation = new Reservation(
                    null,
                    passenger,
                    number,
                    train,
                    sourceStation,
                    destinationStation,
                    journeyDate,
                    (String) classes.getSelectedItem()
            );

            try {
                boolean saved = new ReservationDAO().save(reservation);

                if (saved) {
                    message(frame,
                            "Reservation confirmed!\n\n"
                                    + "PNR: " + reservation.getPnr()
                                    + "\nPassenger: " + passenger
                                    + "\nTrain: " + train + " (" + number + ")"
                                    + "\nRoute: " + sourceStation
                                    + " → " + destinationStation
                                    + "\nJourney date: " + journeyDate
                                    + "\nTravel class: "
                                    + classes.getSelectedItem(),
                            "Booking Confirmation",
                            JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else {
                    message(frame,
                            "Reservation could not be saved.",
                            "Booking Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException | IllegalStateException ex) {
                message(frame,
                        "Could not save reservation:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(clear);
        buttons.add(save);
        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // ---------------- VIEW RESERVATIONS ----------------

    private static void showReservations(JFrame parent) {
        try {
            List<Reservation> reservations =
                    new ReservationDAO().findAll();

            if (reservations.isEmpty()) {
                message(parent,
                        "No reservations found.",
                        "View Reservations",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{
                        "PNR", "Passenger", "Train No.", "Train Name",
                        "From", "To", "Journey Date", "Class", "Status"
                    }, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Reservation r : reservations) {
                model.addRow(new Object[]{
                    r.getPnr(),
                    r.getPassengerName(),
                    r.getTrainNumber(),
                    r.getTrainName(),
                    r.getSourceStation(),
                    r.getDestinationStation(),
                    r.getJourneyDate(),
                    r.getTravelClass(),
                    r.getBookingStatus()
                });
            }

            JTable table = styledTable(model);
            table.setAutoCreateRowSorter(true);

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(BG);
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel title = new JLabel("Saved train reservations");
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            title.setForeground(DARK);

            panel.add(title, BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                    parent,
                    panel,
                    "View Reservations",
                    JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException | IllegalStateException ex) {
            message(parent,
                    "Could not load reservations:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- CANCEL TICKET BY PNR ----------------

    private static void openCancellationScreen(JFrame parent) {
        JFrame frame = new JFrame("RailPlan | Cancel Ticket by PNR");
        frame.setSize(650, 430);
        frame.setMinimumSize(new Dimension(580, 360));
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel title = new JLabel("Find reservation by PNR");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(DARK);
        root.add(title, BorderLayout.NORTH);

        JPanel center = card();
        center.setBorder(new EmptyBorder(18, 18, 18, 18));
        center.setLayout(new BorderLayout(0, 12));

        JTextField pnrField = styledTextField("");
        JButton fetch = primaryButton("Fetch Booking");

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setOpaque(false);
        searchRow.add(pnrField, BorderLayout.CENTER);
        searchRow.add(fetch, BorderLayout.EAST);
        center.add(searchRow, BorderLayout.NORTH);

        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(NORMAL);
        details.setBackground(new Color(248, 250, 252));
        details.setBorder(new EmptyBorder(12, 12, 12, 12));
        details.setText("Enter a PNR and select Fetch Booking.");

        center.add(new JScrollPane(details), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JButton cancel = primaryButton("Confirm Cancellation");
        JButton close = secondaryButton("Close");
        cancel.setEnabled(false);

        final Reservation[] selected = new Reservation[1];

        fetch.addActionListener(e -> {
            String pnr = pnrField.getText().trim();

            if (pnr.isBlank()) {
                message(frame,
                        "Enter a PNR.",
                        "Missing PNR",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Reservation reservation =
                        new ReservationDAO().findByPnr(pnr);

                if (reservation == null) {
                    selected[0] = null;
                    cancel.setEnabled(false);
                    details.setText("No reservation found for PNR: " + pnr);
                    return;
                }

                selected[0] = reservation;
                details.setText(formatReservation(reservation));

                boolean confirmed = "CONFIRMED".equalsIgnoreCase(
                        reservation.getBookingStatus());

                cancel.setEnabled(confirmed);

                if (!confirmed) {
                    details.append(
                            "\n\nThis reservation is already cancelled.");
                }

            } catch (SQLException | IllegalStateException ex) {
                message(frame,
                        "Could not find reservation:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancel.addActionListener(e -> {
            Reservation reservation = selected[0];

            if (reservation == null) {
                return;
            }

            int answer = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to cancel PNR "
                            + reservation.getPnr() + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                boolean cancelled = new ReservationDAO().cancelByPnr(
                        reservation.getPnr());

                if (cancelled) {
                    message(frame,
                            "Reservation " + reservation.getPnr()
                                    + " was cancelled successfully.",
                            "Cancellation Complete",
                            JOptionPane.INFORMATION_MESSAGE);

                    selected[0] = null;
                    cancel.setEnabled(false);
                    details.setText(
                            "Reservation cancelled. You can fetch another PNR.");
                } else {
                    message(frame,
                            "Reservation could not be cancelled. "
                                    + "It may already be cancelled.",
                            "Cancellation Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException | IllegalStateException ex) {
                message(frame,
                        "Could not cancel reservation:\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        close.addActionListener(e -> frame.dispose());
        pnrField.addActionListener(e -> fetch.doClick());

        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(close);
        footer.add(cancel);
        root.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static String formatReservation(Reservation r) {
        return "PNR: " + r.getPnr()
                + "\nPassenger: " + r.getPassengerName()
                + "\nTrain: " + r.getTrainName()
                + " (" + r.getTrainNumber() + ")"
                + "\nFrom: " + r.getSourceStation()
                + "\nTo: " + r.getDestinationStation()
                + "\nJourney date: " + r.getJourneyDate()
                + "\nTravel class: " + r.getTravelClass()
                + "\nStatus: " + r.getBookingStatus();
    }

        // ---------------- CITY AUTOCOMPLETE ----------------

    private static JComboBox<String> cityCombo() {
        JComboBox<String> combo = new JComboBox<>(CITIES);
        combo.setEditable(true);
        combo.setSelectedItem("");
        combo.setMaximumRowCount(8);
        styleCombo(combo);

        JTextField editor = (JTextField)
                combo.getEditor().getEditorComponent();

        editor.setFont(NORMAL);
        editor.setToolTipText("Type a city to see suggestions");

        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                int key = e.getKeyCode();

                if (key == java.awt.event.KeyEvent.VK_UP
                        || key == java.awt.event.KeyEvent.VK_DOWN
                        || key == java.awt.event.KeyEvent.VK_ENTER
                        || key == java.awt.event.KeyEvent.VK_ESCAPE
                        || key == java.awt.event.KeyEvent.VK_LEFT
                        || key == java.awt.event.KeyEvent.VK_RIGHT) {
                    return;
                }

                String typed = editor.getText();
                String search = typed.trim().toLowerCase();

                if (search.isEmpty()) {
                    combo.hidePopup();
                    return;
                }

                List<String> matches = new ArrayList<>();

                for (String city : CITIES) {
                    if (city.toLowerCase().startsWith(search)) {
                        matches.add(city);
                    }
                }

                DefaultComboBoxModel<String> model =
                        new DefaultComboBoxModel<>();

                for (String city : matches) {
                    model.addElement(city);
                }

                combo.setModel(model);
                combo.setSelectedItem(typed);
                combo.getEditor().setItem(typed);
                editor.setText(typed);
                editor.setCaretPosition(typed.length());

                if (!matches.isEmpty()) {
                    combo.showPopup();
                } else {
                    combo.hidePopup();
                }
            }
        });

        return combo;
    }

    private static String comboText(JComboBox<String> combo) {
        Object value = combo.getEditor().getItem();
        return value == null ? "" : value.toString().trim();
    }

    // ---------------- DATE VALIDATION ----------------

    private static LocalDate parseJourneyDate(
            Component parent,
            JTextField date) {

        try {
            String value = date.getText().trim();

            if (value.isBlank()) {
                throw new DateTimeParseException(
                        "Missing date", value, 0);
            }

            LocalDate parsed = LocalDate.parse(value);

            if (parsed.isBefore(LocalDate.now())) {
                message(parent,
                        "Journey date cannot be in the past.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return parsed;

        } catch (DateTimeParseException ex) {
            message(parent,
                    "Enter a valid date in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static void chooseDate(
            Component parent,
            JTextField target) {

        JSpinner spinner = new JSpinner(new SpinnerDateModel());

        spinner.setEditor(
                new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));

        try {
            spinner.setValue(
                    java.sql.Date.valueOf(
                            LocalDate.parse(target.getText().trim())));
        } catch (Exception ex) {
            spinner.setValue(
                    java.sql.Date.valueOf(
                            LocalDate.now().plusDays(7)));
        }

        int result = JOptionPane.showConfirmDialog(
                parent,
                spinner,
                "Choose journey date",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            java.util.Date selected =
                    (java.util.Date) spinner.getValue();

            target.setText(
                    new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(selected));
        }
    }

    // ---------------- FORM LAYOUT HELPER ----------------

    private static void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent input) {

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.4;

        JLabel caption = new JLabel(label);
        caption.setFont(BOLD);
        caption.setForeground(DARK);

        panel.add(caption, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;

        panel.add(input, gbc);
    }

    // ---------------- UI COMPONENT HELPERS ----------------

    private static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(
                BorderFactory.createLineBorder(BORDER));
        return panel;
    }

    private static JPanel field(
            String label,
            JComponent input) {

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel caption = new JLabel(label);
        caption.setFont(new Font("Segoe UI", Font.BOLD, 10));
        caption.setForeground(MUTED);

        panel.add(caption, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel infoRow(
            String title,
            String description) {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(
                panel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);
        heading.setFont(BOLD);
        heading.setForeground(DARK);

        JLabel detail = new JLabel(
                "<html><div style='width:300px'>"
                        + description
                        + "</div></html>");
        detail.setFont(NORMAL);
        detail.setForeground(MUTED);

        panel.add(heading);
        panel.add(Box.createVerticalStrut(4));
        panel.add(detail);

        return panel;
    }

    private static JButton actionButton(
            String title,
            String description) {

        JButton button = new JButton();
        button.setLayout(new BorderLayout(12, 0));
        button.setBackground(Color.WHITE);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(15, 14, 15, 14)));

        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(
                textPanel, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel(title);
        heading.setFont(BOLD);
        heading.setForeground(DARK);

        JLabel detail = new JLabel(description);
        detail.setFont(NORMAL);
        detail.setForeground(MUTED);

        textPanel.add(heading);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(detail);

        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Segoe UI", Font.BOLD, 24));
        arrow.setForeground(BLUE);

        button.add(textPanel, BorderLayout.CENTER);
        button.add(arrow, BorderLayout.EAST);

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 75));

        return button;
    }

    private static JButton primaryButton(String text) {
        JButton button = new JButton(text);

        button.setFont(BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setBorder(
                new EmptyBorder(11, 16, 11, 16));

        return button;
    }

    private static JButton secondaryButton(String text) {
        JButton button = new JButton(text);

        button.setFont(BOLD);
        button.setForeground(DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(9, 12, 9, 12)));

        return button;
    }

    private static JComboBox<String> styledCombo(String[] values) {
        JComboBox<String> combo = new JComboBox<>(values);
        styleCombo(combo);
        return combo;
    }

    private static void styleCombo(JComboBox<String> combo) {
        combo.setFont(NORMAL);
        combo.setForeground(DARK);
        combo.setBackground(Color.WHITE);
        combo.setBorder(
                BorderFactory.createLineBorder(BORDER));
    }

    private static JTextField styledTextField(String text) {
        JTextField field = new JTextField(text);
        styleInput(field);
        return field;
    }

    private static void stylePasswordField(JPasswordField field) {
        field.setFont(NORMAL);
        field.setForeground(DARK);
        field.setBackground(Color.WHITE);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(9, 10, 9, 10)));
    }

    private static void styleInput(JTextField field) {
        field.setFont(NORMAL);
        field.setForeground(DARK);
        field.setBackground(Color.WHITE);

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(9, 10, 9, 10)));
    }

    private static JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);

        table.setFont(NORMAL);
        table.setForeground(DARK);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(DARK);
        table.setRowHeight(34);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);

        javax.swing.table.JTableHeader header =
                table.getTableHeader();

        header.setFont(BOLD);
        header.setForeground(DARK);
        header.setBackground(new Color(241, 245, 249));
        header.setPreferredSize(new Dimension(100, 38));

        return table;
    }

    private static void message(
            Component parent,
            String text,
            String title,
            int type) {

        JOptionPane.showMessageDialog(
                parent, text, title, type);
    }

    // ---------------- SCENIC BACKGROUND PANEL ----------------

    private static class ImageBackgroundPanel extends JPanel {
        private final Image backgroundImage;

        ImageBackgroundPanel() {
            java.net.URL imageUrl =
                    App.class.getResource("/images/railplan_bg.png");

            backgroundImage = imageUrl == null
                    ? null
                    : new ImageIcon(imageUrl).getImage();

            setOpaque(true);
            setBackground(BG);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (backgroundImage != null) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                g2.drawImage(
                        backgroundImage,
                        0, 0,
                        getWidth(), getHeight(),
                        this);

                g2.setColor(new Color(10, 25, 50, 125));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.dispose();
            }
        }
    }
}