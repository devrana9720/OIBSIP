package com.oibsip;

import com.oibsip.reservation.dao.ReservationDAO;
import com.oibsip.reservation.dao.TransportReservationDAO;
import com.oibsip.reservation.model.Reservation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class App {

    private static final Color BG = new Color(245, 247, 251);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color DARK = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    private static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font BOLD = new Font("Segoe UI", Font.BOLD, 13);

    private static final String[] CITIES = {
        "Agra", "Ahmedabad", "Amritsar", "Bengaluru", "Bhopal",
        "Chandigarh", "Chennai", "Dehradun", "Delhi", "Gurugram",
        "Hyderabad", "Jaipur", "Jalandhar", "Kanpur", "Kolkata",
        "Lucknow", "Ludhiana", "Mumbai", "Noida", "Patna", "Prayagraj",
        "Pune", "Rajpura", "Saharanpur", "Shimla", "Surat", "Varanasi"
    };

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::createHomeScreen);
    }

    private static void createHomeScreen() {
        JFrame frame = new JFrame("RailPlan | Online Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 760);
        frame.setMinimumSize(new Dimension(900, 650));
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel nav = new JPanel(new BorderLayout(20, 0));
        nav.setBackground(Color.WHITE);
        nav.setBorder(new EmptyBorder(18, 28, 18, 28));

        JLabel brand = new JLabel("RailPlan");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 23));
        brand.setForeground(BLUE);
        nav.add(brand, BorderLayout.WEST);

        JPanel tabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tabs.setOpaque(false);

        JButton trainTab = navButton("Train", true);
        JButton flightTab = navButton("Flight", false);
        JButton busTab = navButton("Bus", false);

        tabs.add(trainTab);
        tabs.add(flightTab);
        tabs.add(busTab);
        nav.add(tabs, BorderLayout.CENTER);

        flightTab.addActionListener(e -> showTransportDemo(frame, "Flight"));
        busTab.addActionListener(e -> showTransportDemo(frame, "Bus"));

        JLabel demo = new JLabel("DEMO SIMULATOR");
        demo.setFont(BOLD);
        demo.setForeground(BLUE);
        demo.setOpaque(true);
        demo.setBackground(new Color(239, 246, 255));
        demo.setBorder(new EmptyBorder(10, 12, 10, 12));
        nav.add(demo, BorderLayout.EAST);

        root.add(nav, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(24, 28, 18, 28));

        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));

        JLabel heading = new JLabel("Plan your next journey");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(DARK);

        JLabel subtitle = new JLabel(
            "Explore routes and manage your simulated reservations."
        );
        subtitle.setFont(NORMAL);
        subtitle.setForeground(MUTED);

        intro.add(heading);
        intro.add(Box.createVerticalStrut(5));
        intro.add(subtitle);

        JPanel searchCard = card();
        searchCard.setLayout(new BorderLayout(0, 18));
        searchCard.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel searchTitle = new JLabel("Search your journey");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchTitle.setForeground(DARK);
        searchCard.add(searchTitle, BorderLayout.NORTH);

        JComboBox<String> from = cityCombo();
        JComboBox<String> to = cityCombo();

        JTextField date = styledTextField(
            LocalDate.now().plusDays(7).toString()
        );
        date.setToolTipText("Enter date as YYYY-MM-DD");

        JButton calendar = secondaryButton("Calendar");
        calendar.addActionListener(e -> chooseDate(frame, date));

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.add(date, BorderLayout.CENTER);
        datePanel.add(calendar, BorderLayout.EAST);

        JComboBox<String> travelClass = styledCombo(new String[]{
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });

        JPanel fields = new JPanel(new GridLayout(1, 4, 12, 0));
        fields.setOpaque(false);
        fields.add(field("FROM", from));
        fields.add(field("TO", to));
        fields.add(field("JOURNEY DATE (YYYY-MM-DD)", datePanel));
        fields.add(field("TRAVEL CLASS", travelClass));

        JButton search = primaryButton("Search Routes");

        search.addActionListener(e -> {
            String source = comboText(from);
            String destination = comboText(to);
            LocalDate journeyDate = parseJourneyDate(frame, date);

            if (journeyDate == null) {
                return;
            }

            if (source.isBlank() || destination.isBlank()) {
                message(
                    frame,
                    "Enter both departure and destination cities.",
                    "Missing Route",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (source.equalsIgnoreCase(destination)) {
                message(
                    frame,
                    "Departure and destination must differ.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            showSampleResults(
                frame,
                source,
                destination,
                journeyDate,
                (String) travelClass.getSelectedItem()
            );
        });

        JPanel searchBottom = new JPanel(new BorderLayout());
        searchBottom.setOpaque(false);

        JLabel hint = new JLabel(
            "Demo only — no real tickets or payments."
        );
        hint.setFont(NORMAL);
        hint.setForeground(MUTED);

        searchBottom.add(hint, BorderLayout.WEST);
        searchBottom.add(search, BorderLayout.EAST);

        JPanel searchCenter = new JPanel(new BorderLayout(0, 18));
        searchCenter.setOpaque(false);
        searchCenter.add(fields, BorderLayout.CENTER);
        searchCenter.add(searchBottom, BorderLayout.SOUTH);
        searchCard.add(searchCenter, BorderLayout.CENTER);

        JPanel lower = new JPanel(new GridLayout(1, 2, 18, 0));
        lower.setOpaque(false);

        JPanel actions = card();
        actions.setLayout(new BorderLayout(0, 15));
        actions.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel actionsTitle = new JLabel("Manage reservations");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        actionsTitle.setForeground(DARK);
        actions.add(actionsTitle, BorderLayout.NORTH);

        JPanel actionList = new JPanel();
        actionList.setOpaque(false);
        actionList.setLayout(new BoxLayout(actionList, BoxLayout.Y_AXIS));

        JButton book = actionButton(
            "Book a reservation",
            "Enter passenger and journey details"
        );

        JButton view = actionButton(
            "View reservations",
            "Review reservations saved in MySQL"
        );

        JButton cancel = actionButton(
            "Cancel reservation",
            "Select and cancel a reservation"
        );

        book.addActionListener(e -> openBookingForm(frame));
        view.addActionListener(e -> showReservations(frame));
        cancel.addActionListener(e -> openCancellationScreen(frame));

        actionList.add(book);
        actionList.add(Box.createVerticalStrut(10));
        actionList.add(view);
        actionList.add(Box.createVerticalStrut(10));
        actionList.add(cancel);

        actions.add(actionList, BorderLayout.CENTER);

        JPanel info = card();
        info.setLayout(new BorderLayout(0, 18));
        info.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel infoTitle = new JLabel("Your travel workspace");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        infoTitle.setForeground(DARK);
        info.add(infoTitle, BorderLayout.NORTH);

        JPanel infoRows = new JPanel();
        infoRows.setOpaque(false);
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));

        infoRows.add(infoRow(
            "Simple booking flow",
            "Save Train reservations using your existing database workflow."
        ));

        infoRows.add(Box.createVerticalStrut(20));

        infoRows.add(infoRow(
            "Organized records",
            "View saved records in a sortable table."
        ));

        infoRows.add(Box.createVerticalStrut(20));

        infoRows.add(infoRow(
            "Safe simulation",
            "No genuine tickets or payments are processed."
        ));

        info.add(infoRows, BorderLayout.CENTER);

        lower.add(actions);
        lower.add(info);

        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);
        center.add(intro, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 18));
        middle.setOpaque(false);
        middle.add(searchCard, BorderLayout.NORTH);
        middle.add(lower, BorderLayout.CENTER);
        center.add(middle, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);
        root.add(content, BorderLayout.CENTER);

        JLabel footer = new JLabel(
            "RailPlan  •  Indian travel simulator  •  Sample experience only"
        );
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(MUTED);
        footer.setBorder(new EmptyBorder(0, 28, 14, 28));
        root.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // =========================================================
    // TRAIN SEARCH RESULTS
    // =========================================================

    private static void showSampleResults(
            JFrame parent,
            String from,
            String to,
            LocalDate date,
            String travelClass) {

        JDialog dialog = new JDialog(
            parent, "Train Search Results", true
        );

        dialog.setSize(1250, 760);
        dialog.setMinimumSize(new Dimension(1000, 620));
        dialog.setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(20, 22, 20, 22));

        JLabel title = new JLabel("Find your train");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
            from + "  →  " + to + "  |  " + date + "  |  " + travelClass
        );
        subtitle.setFont(NORMAL);
        subtitle.setForeground(MUTED);

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(subtitle);

        JButton close = secondaryButton("Close Results");
        close.addActionListener(e -> dialog.dispose());

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(headerText, BorderLayout.CENTER);
        header.add(close, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JPanel filters = card();
        filters.setPreferredSize(new Dimension(190, 0));
        filters.setLayout(new BoxLayout(filters, BoxLayout.Y_AXIS));
        filters.setBorder(new EmptyBorder(16, 14, 16, 14));

        JLabel filterTitle = new JLabel("Filters");
        filterTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        filterTitle.setForeground(DARK);

        JComboBox<String> price = styledCombo(new String[]{
            "Any price", "Up to ₹500", "Up to ₹650", "Up to ₹800"
        });

        JCheckBox sleeper = filterCheck("Sleeper");
        JCheckBox ac3 = filterCheck("AC 3 Tier");
        JCheckBox ac2 = filterCheck("AC 2 Tier");
        JCheckBox ac1 = filterCheck("AC First Class");

        JCheckBox morning = filterCheck("Morning");
        JCheckBox afternoon = filterCheck("Afternoon");
        JCheckBox evening = filterCheck("Evening");

        for (JCheckBox check : new JCheckBox[]{
            sleeper, ac3, ac2, ac1, morning, afternoon, evening
        }) {
            check.setSelected(true);
        }

        JButton reset = secondaryButton("Reset filters");

        filters.add(filterTitle);
        filters.add(Box.createVerticalStrut(16));
        filters.add(sectionLabel("PRICE RANGE"));
        filters.add(Box.createVerticalStrut(6));
        filters.add(price);
        filters.add(Box.createVerticalStrut(18));
        filters.add(sectionLabel("TRAVEL CLASS"));
        filters.add(Box.createVerticalStrut(6));
        filters.add(sleeper);
        filters.add(ac3);
        filters.add(ac2);
        filters.add(ac1);
        filters.add(Box.createVerticalStrut(18));
        filters.add(sectionLabel("DEPARTURE"));
        filters.add(Box.createVerticalStrut(6));
        filters.add(morning);
        filters.add(afternoon);
        filters.add(evening);
        filters.add(Box.createVerticalStrut(18));
        filters.add(reset);

        JLabel resultsTitle = new JLabel("Available sample trains");
        resultsTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        resultsTitle.setForeground(DARK);

        JComboBox<String> sort = styledCombo(new String[]{
            "Recommended", "Lowest fare", "Earliest departure"
        });

        JPanel resultsHeader = new JPanel(new BorderLayout(8, 0));
        resultsHeader.setOpaque(false);
        resultsHeader.add(resultsTitle, BorderLayout.CENTER);
        resultsHeader.add(sort, BorderLayout.EAST);

        JPanel resultCards = new JPanel();
        resultCards.setOpaque(false);
        resultCards.setLayout(new BoxLayout(resultCards, BoxLayout.Y_AXIS));

        JScrollPane resultScroll = new JScrollPane(resultCards);
        resultScroll.setBorder(BorderFactory.createEmptyBorder());
        resultScroll.getViewport().setBackground(BG);
        resultScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(resultsHeader, BorderLayout.NORTH);
        center.add(resultScroll, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(200, 0));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JPanel offers = card();
        offers.setLayout(new BoxLayout(offers, BoxLayout.Y_AXIS));
        offers.setBorder(new EmptyBorder(16, 14, 16, 14));

        JLabel offersTitle = new JLabel("Demo offers");
        offersTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        offersTitle.setForeground(DARK);

        offers.add(offersTitle);
        offers.add(Box.createVerticalStrut(16));

        offers.add(infoRow(
            "Travel Saver",
            "Illustrative discount on selected sample fares."
        ));

        offers.add(Box.createVerticalStrut(18));

        offers.add(infoRow(
            "Early Planner",
            "Compare estimated fares for sample routes."
        ));

        JPanel availability = card();
        availability.setLayout(new BoxLayout(availability, BoxLayout.Y_AXIS));
        availability.setBorder(new EmptyBorder(16, 14, 16, 14));

        JLabel availabilityTitle = new JLabel("Sample availability");
        availabilityTitle.setFont(BOLD);
        availabilityTitle.setForeground(DARK);

        JLabel availabilityValue = new JLabel("75%");
        availabilityValue.setFont(new Font("Segoe UI", Font.BOLD, 38));
        availabilityValue.setForeground(BLUE);
        availabilityValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel availabilityNote = new JLabel(
            "<html><div style='width:150px;text-align:center'>"
                + "Illustrative only. No live inventory is checked."
                + "</div></html>"
        );

        availabilityNote.setFont(NORMAL);
        availabilityNote.setForeground(MUTED);
        availabilityNote.setAlignmentX(Component.CENTER_ALIGNMENT);

        availability.add(availabilityTitle);
        availability.add(Box.createVerticalStrut(20));
        availability.add(availabilityValue);
        availability.add(Box.createVerticalStrut(8));
        availability.add(availabilityNote);

        right.add(offers);
        right.add(Box.createVerticalStrut(16));
        right.add(availability);

        String[][] sampleData = {
            {"Sample Express", "06:15", "12:00", "5h 45m", "450", "Sleeper"},
            {"Intercity Demo", "10:30", "16:45", "6h 15m", "620", "AC 3 Tier"},
            {"Superfast Demo", "17:45", "23:10", "5h 25m", "780", "AC 2 Tier"},
            {"Comfort Express", "13:10", "19:20", "6h 10m", "550", "AC 3 Tier"},
            {"City Link Demo", "08:00", "14:30", "6h 30m", "490", "Sleeper"}
        };

        Runnable updateResults = () -> {
            resultCards.removeAll();

            int maxFare = Integer.MAX_VALUE;
            String selectedPrice = (String) price.getSelectedItem();

            if ("Up to ₹500".equals(selectedPrice)) {
                maxFare = 500;
            }

            if ("Up to ₹650".equals(selectedPrice)) {
                maxFare = 650;
            }

            if ("Up to ₹800".equals(selectedPrice)) {
                maxFare = 800;
            }

            List<String[]> matching = new ArrayList<>();

            for (String[] train : sampleData) {
                int fare = Integer.parseInt(train[4]);
                int hour = Integer.parseInt(train[1].substring(0, 2));

                boolean classOk =
                    (train[5].equals("Sleeper") && sleeper.isSelected())
                    || (train[5].equals("AC 3 Tier") && ac3.isSelected())
                    || (train[5].equals("AC 2 Tier") && ac2.isSelected())
                    || (train[5].equals("AC First Class") && ac1.isSelected());

                boolean timeOk =
                    (hour < 12 && morning.isSelected())
                    || (hour >= 12 && hour < 17 && afternoon.isSelected())
                    || (hour >= 17 && evening.isSelected());

                if (fare <= maxFare && classOk && timeOk) {
                    matching.add(train);
                }
            }

            String selectedSort = (String) sort.getSelectedItem();

            if ("Lowest fare".equals(selectedSort)) {
                matching.sort(Comparator.comparingInt(
                    train -> Integer.parseInt(train[4])
                ));
            } else if ("Earliest departure".equals(selectedSort)) {
                matching.sort(Comparator.comparing(train -> train[1]));
            }

            for (String[] train : matching) {
                JPanel route = card();
                route.setLayout(new BorderLayout(12, 10));
                route.setBorder(new EmptyBorder(15, 14, 15, 14));

                JPanel details = new JPanel();
                details.setOpaque(false);
                details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

                JLabel trainName = new JLabel(train[0]);
                trainName.setFont(BOLD);
                trainName.setForeground(DARK);

                JLabel times = new JLabel(
                    train[1] + "     ─────────     " + train[2]
                );
                times.setFont(new Font("Segoe UI", Font.BOLD, 19));
                times.setForeground(DARK);

                JLabel routeText = new JLabel(from + "  →  " + to);
                routeText.setFont(NORMAL);
                routeText.setForeground(MUTED);

                JLabel duration = new JLabel(
                    "Estimated duration: " + train[3] + "  |  " + train[5]
                );
                duration.setFont(NORMAL);
                duration.setForeground(MUTED);

                details.add(trainName);
                details.add(Box.createVerticalStrut(8));
                details.add(times);
                details.add(Box.createVerticalStrut(5));
                details.add(routeText);
                details.add(Box.createVerticalStrut(5));
                details.add(duration);

                JPanel action = new JPanel();
                action.setOpaque(false);
                action.setLayout(new BoxLayout(action, BoxLayout.Y_AXIS));

                JLabel fare = new JLabel("₹" + train[4] + " / person");
                fare.setFont(new Font("Segoe UI", Font.BOLD, 18));
                fare.setForeground(new Color(234, 88, 12));

                JButton continueButton = primaryButton("Continue to Booking");

                continueButton.addActionListener(e -> {
                    dialog.dispose();
                    openBookingForm(parent, date.toString(), travelClass);
                });

                action.add(fare);
                action.add(Box.createVerticalStrut(10));
                action.add(continueButton);

                route.add(details, BorderLayout.CENTER);
                route.add(action, BorderLayout.EAST);
                route.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

                resultCards.add(route);
                resultCards.add(Box.createVerticalStrut(12));
            }

            if (matching.isEmpty()) {
                JLabel empty = new JLabel(
                    "No sample trains match your filters. Reset or change filters."
                );
                empty.setForeground(MUTED);
                empty.setFont(NORMAL);
                empty.setBorder(new EmptyBorder(20, 10, 20, 10));
                resultCards.add(empty);
            }

            resultsTitle.setText(
                "Available sample trains (" + matching.size() + ")"
            );

            resultCards.revalidate();
            resultCards.repaint();
        };

        reset.addActionListener(e -> {
            price.setSelectedIndex(0);

            for (JCheckBox check : new JCheckBox[]{
                sleeper, ac3, ac2, ac1, morning, afternoon, evening
            }) {
                check.setSelected(true);
            }

            sort.setSelectedIndex(0);
            updateResults.run();
        });

        price.addActionListener(e -> updateResults.run());

        for (JCheckBox check : new JCheckBox[]{
            sleeper, ac3, ac2, ac1, morning, afternoon, evening
        }) {
            check.addActionListener(e -> updateResults.run());
        }

        sort.addActionListener(e -> updateResults.run());

        JPanel body = new JPanel(new BorderLayout(14, 0));
        body.setOpaque(false);
        body.add(filters, BorderLayout.WEST);
        body.add(center, BorderLayout.CENTER);
        body.add(right, BorderLayout.EAST);

        root.add(body, BorderLayout.CENTER);

        JLabel disclaimer = new JLabel(
            "Demo simulator • Sample routes, fares and availability only"
        );
        disclaimer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        disclaimer.setForeground(MUTED);
        root.add(disclaimer, BorderLayout.SOUTH);

        updateResults.run();
        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // =========================================================
    // FLIGHT AND BUS SEARCH
    // =========================================================

    private static void showTransportDemo(JFrame parent, String type) {

        String transportType =
            type.equalsIgnoreCase("Flight") ? "FLIGHT" : "BUS";

        JDialog dialog = new JDialog(parent, type + " Simulator", true);
        dialog.setSize(1100, 740);
        dialog.setMinimumSize(new Dimension(900, 620));
        dialog.setLocationRelativeTo(parent);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(
            "Explore sample " + type.toLowerCase() + " journeys"
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
            "Compare illustrative routes and estimated fares. Demo only."
        );
        subtitle.setFont(NORMAL);
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        root.add(heading, BorderLayout.NORTH);

        JPanel searchCard = card();
        searchCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        searchCard.setLayout(new BorderLayout(0, 14));

        JLabel searchTitle = new JLabel("Search your journey");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        searchTitle.setForeground(DARK);
        searchCard.add(searchTitle, BorderLayout.NORTH);

        JComboBox<String> from = cityCombo();
        JComboBox<String> to = cityCombo();

        JTextField date = styledTextField(
            LocalDate.now().plusDays(7).toString()
        );

        JButton calendar = secondaryButton("Calendar");
        calendar.addActionListener(e -> chooseDate(dialog, date));

        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.add(date, BorderLayout.CENTER);
        datePanel.add(calendar, BorderLayout.EAST);

        String[] classes = transportType.equals("FLIGHT")
            ? new String[]{
                "Economy", "Premium Economy", "Business", "First Class"
            }
            : new String[]{
                "Ordinary", "AC Seater", "AC Sleeper", "Volvo"
            };

        JComboBox<String> travelClass = styledCombo(classes);

        JPanel fields = new JPanel(new GridLayout(1, 4, 10, 0));
        fields.setOpaque(false);
        fields.add(field("FROM", from));
        fields.add(field("TO", to));
        fields.add(field("JOURNEY DATE (YYYY-MM-DD)", datePanel));
        fields.add(field("TRAVEL CLASS", travelClass));

        JPanel searchBottom = new JPanel(new BorderLayout());
        searchBottom.setOpaque(false);

        JLabel disclaimer = new JLabel(
            "Sample services only — no real tickets or payments."
        );
        disclaimer.setForeground(MUTED);
        searchBottom.add(disclaimer, BorderLayout.CENTER);

        JButton search = primaryButton("Search " + type + "s");
        searchBottom.add(search, BorderLayout.EAST);

        JPanel searchContent = new JPanel(new BorderLayout(0, 14));
        searchContent.setOpaque(false);
        searchContent.add(fields, BorderLayout.CENTER);
        searchContent.add(searchBottom, BorderLayout.SOUTH);
        searchCard.add(searchContent, BorderLayout.CENTER);

        JPanel results = new JPanel();
        results.setOpaque(false);
        results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(results);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(searchCard, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        root.add(center, BorderLayout.CENTER);

        search.addActionListener(e -> {

            String source = comboText(from);
            String destination = comboText(to);

            if (source.isBlank() || destination.isBlank()) {
                message(
                    dialog,
                    "Enter both departure and destination cities.",
                    "Missing Route",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (source.equalsIgnoreCase(destination)) {
                message(
                    dialog,
                    "Departure and destination must differ.",
                    "Invalid Route",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            LocalDate journeyDate = parseJourneyDate(dialog, date);

            if (journeyDate == null) {
                return;
            }

            results.removeAll();

            String[][] samples = transportType.equals("FLIGHT")
                ? new String[][]{
                    {"SkyConnect Demo", "06:30", "08:10", "1h 40m", "3499"},
                    {"Indian Wings Sample", "11:15", "13:00", "1h 45m", "4250"},
                    {"AirRoute Demo", "17:40", "19:25", "1h 45m", "5100"}
                }
                : new String[][]{
                    {"RoadStar Express", "06:00", "12:30", "6h 30m", "650"},
                    {"Comfort Travels Demo", "10:15", "17:00", "6h 45m", "850"},
                    {"Night Rider Sample", "21:00", "05:30", "8h 30m", "1100"}
                };

            JLabel resultTitle = new JLabel(
                "Sample " + type + " services: "
                    + source + " → " + destination
                    + "  |  " + journeyDate
            );

            resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
            resultTitle.setForeground(DARK);
            resultTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
            results.add(resultTitle);

            for (String[] sample : samples) {

                JPanel route = card();
                route.setLayout(new BorderLayout(12, 8));
                route.setBorder(new EmptyBorder(16, 16, 16, 16));

                JPanel details = new JPanel();
                details.setOpaque(false);
                details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

                JLabel service = new JLabel(sample[0]);
                service.setFont(new Font("Segoe UI", Font.BOLD, 16));
                service.setForeground(DARK);

                JLabel routeText = new JLabel(
                    source + "  →  " + destination
                );
                routeText.setFont(NORMAL);
                routeText.setForeground(MUTED);

                JLabel times = new JLabel(sample[1] + "  —  " + sample[2]);
                times.setFont(new Font("Segoe UI", Font.BOLD, 20));
                times.setForeground(DARK);

                JLabel duration = new JLabel(
                    "Estimated duration: " + sample[3]
                        + "  |  Class: " + travelClass.getSelectedItem()
                );
                duration.setFont(NORMAL);
                duration.setForeground(MUTED);

                details.add(service);
                details.add(Box.createVerticalStrut(7));
                details.add(routeText);
                details.add(Box.createVerticalStrut(5));
                details.add(times);
                details.add(Box.createVerticalStrut(5));
                details.add(duration);

                JPanel action = new JPanel();
                action.setOpaque(false);
                action.setLayout(new BoxLayout(action, BoxLayout.Y_AXIS));

                BigDecimal fareAmount = new BigDecimal(sample[4]);

                JLabel fare = new JLabel("₹" + fareAmount + " / person");
                fare.setFont(new Font("Segoe UI", Font.BOLD, 19));
                fare.setForeground(BLUE);

                JLabel fareNote = new JLabel("Estimated fare");
                fareNote.setFont(NORMAL);
                fareNote.setForeground(MUTED);

                JButton book = primaryButton("Book Ticket");

                book.addActionListener(ev ->
                    openTransportBooking(
                        dialog,
                        transportType,
                        source,
                        destination,
                        journeyDate,
                        (String) travelClass.getSelectedItem(),
                        sample[0],
                        fareAmount
                    )
                );

                action.add(fare);
                action.add(Box.createVerticalStrut(4));
                action.add(fareNote);
                action.add(Box.createVerticalStrut(10));
                action.add(book);

                route.add(details, BorderLayout.CENTER);
                route.add(action, BorderLayout.EAST);
                route.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

                results.add(route);
                results.add(Box.createVerticalStrut(12));
            }

            results.revalidate();
            results.repaint();
        });

        JButton view = secondaryButton("View Reservations");
        view.addActionListener(e ->
            showTransportReservations(dialog, transportType, false)
        );

        JButton cancel = secondaryButton("Cancel Reservation");
        cancel.addActionListener(e ->
            showTransportReservations(dialog, transportType, true)
        );

        JButton close = secondaryButton("Close");
        close.addActionListener(e -> dialog.dispose());

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.add(view);
        footer.add(cancel);
        footer.add(close);

        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    // =========================================================
    // FLIGHT AND BUS BOOKING
    // =========================================================

    private static void openTransportBooking(
            Component parent,
            String type,
            String source,
            String destination,
            LocalDate date,
            String travelClass,
            String serviceName,
            BigDecimal fare) {

        JTextField passenger = styledTextField("");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 6, 7, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(panel, gbc, 0, "Passenger name", passenger);
        addFormRow(panel, gbc, 1, "Transport", new JLabel(type));
        addFormRow(panel, gbc, 2, "Service", new JLabel(serviceName));
        addFormRow(
            panel, gbc, 3, "Route",
            new JLabel(source + " → " + destination)
        );
        addFormRow(panel, gbc, 4, "Journey date", new JLabel(date.toString()));
        addFormRow(panel, gbc, 5, "Travel class", new JLabel(travelClass));
        addFormRow(panel, gbc, 6, "Estimated fare", new JLabel("₹" + fare));

        int choice = JOptionPane.showConfirmDialog(
            parent,
            panel,
            "Book " + type + " Ticket",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = passenger.getText().trim();

        if (name.isBlank()) {
            message(
                parent,
                "Enter the passenger name.",
                "Missing Information",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (date.isBefore(LocalDate.now())) {
            message(
                parent,
                "Journey date cannot be in the past.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int id = new TransportReservationDAO().save(
                type,
                name,
                source,
                destination,
                date,
                travelClass,
                serviceName,
                fare
            );

            message(
                parent,
                "Booking saved successfully!\n\n"
                    + "Reservation ID: " + id
                    + "\nPassenger: " + name
                    + "\nRoute: " + source + " → " + destination
                    + "\nDate: " + date
                    + "\nFare: ₹" + fare,
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException | IllegalStateException ex) {
            message(
                parent,
                "Could not save booking:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // VIEW AND CANCEL FLIGHT/BUS RESERVATIONS
    // =========================================================

    private static void showTransportReservations(
            Component parent,
            String type,
            boolean cancellationMode) {

        try {
            List<TransportReservationDAO.TransportReservation> reservations =
                new TransportReservationDAO().findAll(type);

            if (reservations.isEmpty()) {
                message(
                    parent,
                    "No " + type.toLowerCase() + " reservations found.",
                    cancellationMode
                        ? "Cancel Reservation"
                        : "View Reservations",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            DefaultTableModel model = new DefaultTableModel(
                new String[]{
                    "ID", "Passenger", "From", "To", "Date",
                    "Class", "Service", "Fare (₹)", "Status"
                }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (TransportReservationDAO.TransportReservation r : reservations) {
                model.addRow(new Object[]{
                    r.id(),
                    r.passengerName(),
                    r.sourceCity(),
                    r.destinationCity(),
                    r.journeyDate(),
                    r.travelClass(),
                    r.serviceName(),
                    r.fare(),
                    r.status()
                });
            }

            JTable table = styledTable(model);
            table.setAutoCreateRowSorter(true);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(BG);
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel heading = new JLabel(
                cancellationMode
                    ? "Select a confirmed reservation to cancel"
                    : "Saved " + type.toLowerCase() + " reservations"
            );

            heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
            heading.setForeground(DARK);

            panel.add(heading, BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            if (cancellationMode) {

                int answer = JOptionPane.showConfirmDialog(
                    parent,
                    panel,
                    "Cancel " + type + " Reservation",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (answer != JOptionPane.OK_OPTION) {
                    return;
                }

                int selected = table.getSelectedRow();

                if (selected < 0) {
                    message(
                        parent,
                        "Select a reservation first.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int row = table.convertRowIndexToModel(selected);
                int id = ((Number) model.getValueAt(row, 0)).intValue();
                String status = String.valueOf(model.getValueAt(row, 8));

                if (!"CONFIRMED".equalsIgnoreCase(status)) {
                    message(
                        parent,
                        "Only confirmed reservations can be cancelled.",
                        "Already Cancelled",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                    parent,
                    "Cancel reservation ID " + id + "?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                boolean cancelled = new TransportReservationDAO()
                    .cancelById(id, type);

                message(
                    parent,
                    cancelled
                        ? "Reservation cancelled successfully."
                        : "Reservation could not be cancelled.",
                    "Cancellation",
                    cancelled
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
                );

            } else {
                JOptionPane.showMessageDialog(
                    parent,
                    panel,
                    "View Reservations",
                    JOptionPane.PLAIN_MESSAGE
                );
            }

        } catch (SQLException | IllegalStateException ex) {
            message(
                parent,
                "Could not access reservations:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // TRAIN BOOKING
    // =========================================================

    private static void openBookingForm(JFrame parent) {
        openBookingForm(parent, "", "Sleeper");
    }

    private static void openBookingForm(
            JFrame parent, String date, String travelClass) {

        JFrame frame = new JFrame("Book Train Reservation");
        frame.setSize(540, 460);
        frame.setMinimumSize(new Dimension(480, 420));
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));

        JLabel title = new JLabel("Book a Train reservation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(DARK);
        root.add(title, BorderLayout.NORTH);

        JPanel form = card();
        form.setBorder(new EmptyBorder(22, 22, 22, 22));
        form.setLayout(new GridBagLayout());

        JTextField name = styledTextField("");
        JTextField train = styledTextField("");
        JTextField journey = styledTextField(date);

        JComboBox<String> classes = styledCombo(new String[]{
            "Sleeper", "AC 3 Tier", "AC 2 Tier", "AC First Class"
        });

        classes.setSelectedItem(travelClass);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(9, 5, 9, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFormRow(form, gbc, 0, "Passenger name", name);
        addFormRow(form, gbc, 1, "Train number", train);
        addFormRow(form, gbc, 2, "Journey date (YYYY-MM-DD)", journey);
        addFormRow(form, gbc, 3, "Travel class", classes);

        root.add(form, BorderLayout.CENTER);

        JButton clear = secondaryButton("Clear");
        JButton save = primaryButton("Save Reservation");

        clear.addActionListener(e -> {
            name.setText("");
            train.setText("");
            journey.setText("");
            classes.setSelectedIndex(0);
        });

        save.addActionListener(e -> {

            String passenger = name.getText().trim();
            String number = train.getText().trim();
            String dateText = journey.getText().trim();

            if (passenger.isEmpty() || number.isEmpty() || dateText.isEmpty()) {
                message(
                    frame,
                    "Please fill in all fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!number.matches("\\d+")) {
                message(
                    frame,
                    "Train number must contain digits only.",
                    "Invalid Train Number",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            LocalDate journeyDate;

            try {
                journeyDate = LocalDate.parse(dateText);
            } catch (DateTimeParseException ex) {
                message(
                    frame,
                    "Use YYYY-MM-DD for the journey date.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (journeyDate.isBefore(LocalDate.now())) {
                message(
                    frame,
                    "Journey date cannot be in the past.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Reservation reservation = new Reservation(
                passenger,
                number,
                journeyDate,
                (String) classes.getSelectedItem()
            );

            try {
                boolean saved = new ReservationDAO().save(reservation);

                if (saved) {
                    message(
                        frame,
                        "Reservation saved successfully!\n\n"
                            + "Passenger: " + passenger
                            + "\nTrain number: " + number
                            + "\nJourney date: " + journeyDate
                            + "\nTravel class: " + classes.getSelectedItem(),
                        "Booking Successful",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    name.setText("");
                    train.setText("");
                    journey.setText("");
                    classes.setSelectedIndex(0);

                } else {
                    message(
                        frame,
                        "Reservation could not be saved.",
                        "Booking Failed",
                        JOptionPane.WARNING_MESSAGE
                    );
                }

            } catch (SQLException | IllegalStateException ex) {
                message(
                    frame,
                    "Could not save reservation:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );

            } catch (NumberFormatException ex) {
                message(
                    frame,
                    "Train number is too large.",
                    "Invalid Train Number",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(clear);
        buttons.add(save);

        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.setVisible(true);
    }

    // =========================================================
    // VIEW TRAIN RESERVATIONS
    // =========================================================

    private static void showReservations(JFrame parent) {

        try {
            List<Reservation> reservations = new ReservationDAO().findAll();

            if (reservations.isEmpty()) {
                message(
                    parent,
                    "No reservations found.",
                    "View Reservations",
                    JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }

            DefaultTableModel model = new DefaultTableModel(
                new String[]{
                    "ID", "Passenger", "Train Number",
                    "Journey Date", "Travel Class"
                }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Reservation r : reservations) {
                model.addRow(new Object[]{
                    r.getId(),
                    r.getPassengerName(),
                    r.getTrainNumber(),
                    r.getJourneyDate(),
                    r.getTravelClass()
                });
            }

            JTable table = styledTable(model);
            table.setAutoCreateRowSorter(true);

            JPanel panel = new JPanel(new BorderLayout(0, 12));
            panel.setBackground(BG);
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));

            JLabel title = new JLabel("Saved reservations");
            title.setFont(new Font("Segoe UI", Font.BOLD, 20));
            title.setForeground(DARK);

            panel.add(title, BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JOptionPane.showMessageDialog(
                parent,
                panel,
                "View Reservations",
                JOptionPane.PLAIN_MESSAGE
            );

        } catch (SQLException | IllegalStateException ex) {
            message(
                parent,
                "Could not load reservations:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // CANCEL TRAIN RESERVATIONS
    // =========================================================

    private static void openCancellationScreen(JFrame parent) {

        JFrame frame = new JFrame("Cancel Reservation");
        frame.setSize(900, 480);
        frame.setMinimumSize(new Dimension(750, 400));
        frame.setLocationRelativeTo(parent);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "ID", "Passenger", "Train Number",
                "Journey Date", "Travel Class"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = styledTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JLabel title = new JLabel("Cancel a reservation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(DARK);

        JButton cancel = primaryButton("Cancel Selected Reservation");
        JButton close = secondaryButton("Close");

        Runnable reload = () -> {
            try {
                model.setRowCount(0);

                for (Reservation r : new ReservationDAO().findAll()) {
                    model.addRow(new Object[]{
                        r.getId(),
                        r.getPassengerName(),
                        r.getTrainNumber(),
                        r.getJourneyDate(),
                        r.getTravelClass()
                    });
                }

                cancel.setEnabled(model.getRowCount() > 0);

            } catch (SQLException | IllegalStateException ex) {
                message(
                    frame,
                    "Could not load reservations:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        };

        cancel.addActionListener(e -> {

            int selected = table.getSelectedRow();

            if (selected < 0) {
                message(
                    frame,
                    "Select a reservation first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int row = table.convertRowIndexToModel(selected);
            int id = ((Number) model.getValueAt(row, 0)).intValue();
            String passenger = String.valueOf(model.getValueAt(row, 1));

            int answer = JOptionPane.showConfirmDialog(
                frame,
                "Cancel the reservation for " + passenger
                    + "?\nReservation ID: " + id,
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                boolean deleted = new ReservationDAO().deleteById(id);

                message(
                    frame,
                    deleted
                        ? "Reservation cancelled successfully."
                        : "Reservation was not found.",
                    "Cancellation",
                    deleted
                        ? JOptionPane.INFORMATION_MESSAGE
                        : JOptionPane.WARNING_MESSAGE
                );

                reload.run();

            } catch (SQLException | IllegalStateException ex) {
                message(
                    frame,
                    "Could not cancel reservation:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        close.addActionListener(e -> frame.dispose());

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttons.setOpaque(false);
        buttons.add(close);
        buttons.add(cancel);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(22, 24, 22, 24));
        root.add(title, BorderLayout.NORTH);
        root.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);

        frame.setContentPane(root);
        reload.run();
        frame.setVisible(true);
    }

    // =========================================================
    // CITY AUTOCOMPLETE
    // =========================================================

    private static JComboBox<String> cityCombo() {

        JComboBox<String> combo = new JComboBox<>(CITIES);
        combo.setEditable(true);
        combo.setSelectedItem("");
        combo.setMaximumRowCount(8);
        styleCombo(combo);

        JTextField editor = (JTextField)
            combo.getEditor().getEditorComponent();

        editor.setFont(NORMAL);
        editor.setToolTipText("Type a city or select a suggestion");

        editor.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {

                private void update() {
                    SwingUtilities.invokeLater(() -> {

                        if (!editor.hasFocus()) {
                            return;
                        }

                        String typed = editor.getText().trim();

                        if (typed.isEmpty()) {
                            return;
                        }

                        for (String city : CITIES) {
                            if (city.toLowerCase().startsWith(
                                    typed.toLowerCase())) {

                                if (!combo.isPopupVisible()) {
                                    combo.showPopup();
                                }

                                break;
                            }
                        }
                    });
                }

                @Override
                public void insertUpdate(
                        javax.swing.event.DocumentEvent e) {
                    update();
                }

                @Override
                public void removeUpdate(
                        javax.swing.event.DocumentEvent e) {
                    update();
                }

                @Override
                public void changedUpdate(
                        javax.swing.event.DocumentEvent e) {
                    update();
                }
            }
        );

        return combo;
    }

    private static String comboText(JComboBox<String> combo) {
        Object value = combo.getEditor().getItem();
        return value == null ? "" : value.toString().trim();
    }

    // =========================================================
    // DATE VALIDATION
    // =========================================================

    private static LocalDate parseJourneyDate(
            Component parent, JTextField date) {

        try {
            String text = date.getText().trim();

            if (text.isBlank()) {
                throw new DateTimeParseException(
                    "Missing date", text, 0
                );
            }

            LocalDate parsed = LocalDate.parse(text);

            if (parsed.isBefore(LocalDate.now())) {
                message(
                    parent,
                    "Journey date cannot be in the past.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE
                );
                return null;
            }

            return parsed;

        } catch (DateTimeParseException ex) {
            message(
                parent,
                "Enter a date in YYYY-MM-DD format.",
                "Invalid Date",
                JOptionPane.WARNING_MESSAGE
            );
            return null;
        }
    }

    private static void chooseDate(
            Component parent, JTextField target) {

        JSpinner spinner = new JSpinner(new SpinnerDateModel());

        spinner.setEditor(
            new JSpinner.DateEditor(spinner, "yyyy-MM-dd")
        );

        try {
            LocalDate current = LocalDate.parse(
                target.getText().trim()
            );

            spinner.setValue(java.sql.Date.valueOf(current));

        } catch (Exception ignored) {
            spinner.setValue(
                java.sql.Date.valueOf(LocalDate.now().plusDays(7))
            );
        }

        int result = JOptionPane.showConfirmDialog(
            parent,
            spinner,
            "Choose journey date",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            java.util.Date selected =
                (java.util.Date) spinner.getValue();

            target.setText(
                new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .format(selected)
            );
        }
    }

    // =========================================================
    // FORM HELPERS
    // =========================================================

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

    // =========================================================
    // UI STYLING HELPERS
    // =========================================================

    private static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(
            BorderFactory.createLineBorder(BORDER)
        );
        return panel;
    }

    private static JPanel field(
            String label, JComponent input) {

        JPanel panel = new JPanel(
            new BorderLayout(0, 8)
        );

        panel.setOpaque(false);

        JLabel caption = new JLabel(label);
        caption.setFont(
            new Font("Segoe UI", Font.BOLD, 10)
        );
        caption.setForeground(MUTED);

        panel.add(caption, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    private static JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(
            new Font("Segoe UI", Font.BOLD, 11)
        );
        label.setForeground(MUTED);
        return label;
    }

    private static JCheckBox filterCheck(String text) {
        JCheckBox check = new JCheckBox(text);
        check.setFont(NORMAL);
        check.setForeground(DARK);
        check.setBackground(Color.WHITE);
        check.setFocusPainted(false);
        check.setAlignmentX(Component.LEFT_ALIGNMENT);
        return check;
    }

    private static JPanel infoRow(
            String title, String description) {

        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(
            new BoxLayout(row, BoxLayout.Y_AXIS)
        );

        JLabel heading = new JLabel(title);
        heading.setFont(BOLD);
        heading.setForeground(DARK);

        JLabel detail = new JLabel(
            "<html><div style='width:220px'>"
                + description
                + "</div></html>"
        );

        detail.setFont(NORMAL);
        detail.setForeground(MUTED);
        detail.setBorder(
            new EmptyBorder(4, 0, 0, 0)
        );

        row.add(heading);
        row.add(detail);

        return row;
    }

    private static JButton actionButton(
            String title, String description) {

        JButton button = new JButton();
        button.setLayout(
            new BorderLayout(12, 0)
        );

        button.setBackground(Color.WHITE);
        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 14, 15, 14)
            )
        );

        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(
            new BoxLayout(textPanel, BoxLayout.Y_AXIS)
        );

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
            new Dimension(Integer.MAX_VALUE, 75)
        );

        return button;
    }

    private static JButton navButton(
            String text, boolean selected) {

        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (selected) {
            button.setForeground(Color.WHITE);
            button.setBackground(BLUE);
        } else {
            button.setForeground(DARK);
            button.setBackground(Color.WHITE);
        }

        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    selected ? BLUE : BORDER
                ),
                new EmptyBorder(9, 16, 9, 16)
            )
        );

        return button;
    }

    private static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(
            new EmptyBorder(11, 16, 11, 16)
        );
        return button;
    }

    private static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BOLD);
        button.setForeground(DARK);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 12, 9, 12)
            )
        );
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
        combo.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    private static JTextField styledTextField(String text) {
        JTextField field = new JTextField(text);
        styleInput(field);
        return field;
    }

    private static void styleInput(JTextField field) {
        field.setFont(NORMAL);
        field.setForeground(DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 10, 9, 10)
            )
        );
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

        JTableHeader header = table.getTableHeader();
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
            parent,
            text,
            title,
            type
        );
    }
}