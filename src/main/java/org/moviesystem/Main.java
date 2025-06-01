package org.moviesystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.moviesystem.dao.DatabaseUtil;
import org.moviesystem.dao.MovieDAO;
import org.moviesystem.model.Actor;
import org.moviesystem.model.Director;
import org.moviesystem.model.Movie;
import org.moviesystem.model.Review;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.text.ParseException;

public class Main {
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color CARD_COLOR = Color.WHITE;

    private static JFrame mainFrame;
    private static JTable moviesTable;
    private static DefaultTableModel tableModel;
    private static JTextField titleField, runningTimeField, ratingField, releaseDateField;
    private static JTextField actorsField, directorField;
    private static JTextArea plotArea, reviewsArea;
    private static JTextField searchField;

    private static List<Movie> movieList = new ArrayList<>();

    private static int editingMovieId = -1;
    private static JPanel statsPanel;
    private static JPanel dashboardTab;

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        movieList = MovieDAO.getAllMovies();
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Comedy Movie System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.getContentPane().setBackground(BACKGROUND_COLOR);

        dashboardTab = createDashboardTab();
        mainFrame.add(dashboardTab);
        mainFrame.setVisible(true);
    }

    private static JPanel createDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel actionPanel = createActionButtonsPanel();
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel totalCard = createStatCard("Total Comedy Movies", String.valueOf(movieList.size()), PRIMARY_COLOR);

        double avgRating = movieList.stream().mapToDouble(Movie::getRating).average().orElse(0.0);
        JPanel ratingCard = createStatCard("Avg Comedy Rating", String.format("%.1f/10", avgRating), SUCCESS_COLOR);

        String latestMovie = movieList.isEmpty() ? "None" : movieList.get(movieList.size() - 1).getTitle();
        JPanel latestCard = createStatCard("Latest Added Comedy Movie", latestMovie, SECONDARY_COLOR);

        JPanel actionCard = createQuickActionCard();

        statsPanel.add(totalCard);
        statsPanel.add(ratingCard);
        statsPanel.add(latestCard);
        statsPanel.add(actionCard);

        return statsPanel;
    }

    private static JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private static JPanel createQuickActionCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(WARNING_COLOR, 2),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel("Quick Actions");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton quickAddBtn = createStyledButton("Add Movie", WARNING_COLOR);
        quickAddBtn.addActionListener(e -> createAndShowAddMovieDialog());

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(quickAddBtn, BorderLayout.CENTER);

        return card;
    }

    private static JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Movie Collection"),
                new EmptyBorder(10, 10, 10, 10)
        ));

        createMovieTable();
        JScrollPane scrollPane = new JScrollPane(moviesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(CARD_COLOR);

        JLabel searchLabel = new JLabel("Search:");
        searchField = createStyledTextField(20);
        JButton searchBtn = createStyledButton("Search", SECONDARY_COLOR);
        searchBtn.addActionListener(e -> performSearch());

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(searchBtn);

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private static void createMovieTable() {
        String[] columns = {"ID", "Title", "Running Time", "Comedy Rating", "Director", "Release Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        moviesTable = new JTable(tableModel);
        moviesTable.setRowHeight(25);
        moviesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moviesTable.setSelectionBackground(new Color(174, 214, 241));
        moviesTable.setGridColor(new Color(189, 195, 199));

        JTableHeader header = moviesTable.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 30));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        moviesTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        moviesTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        moviesTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        moviesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        moviesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        moviesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        moviesTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        moviesTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        moviesTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        loadMoviesIntoTable();
    }

    private static void loadMoviesIntoTable() {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Movie movie : movieList) {
            tableModel.addRow(new Object[]{
                movie.getId(),
                movie.getTitle(),
                movie.getRunningTimeMinutes() + " min",
                movie.getRating() + "/10",
                movie.getDirector() != null ? movie.getDirector().getName() : "",
                movie.getReleaseDate() != null ? dateFormat.format(movie.getReleaseDate()) : ""
            });
        }
        refreshStatsPanel();
    }

    private static JPanel createActionButtonsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JButton viewBtn = createStyledButton("View Details", SECONDARY_COLOR);
        JButton editBtn = createStyledButton("Edit Selected", WARNING_COLOR);
        JButton deleteBtn = createStyledButton("Delete Selected", ACCENT_COLOR);

        viewBtn.addActionListener(e -> viewSelectedMovie());
        editBtn.addActionListener(e -> editSelectedMovie());
        deleteBtn.addActionListener(e -> deleteSelectedMovie());

        panel.add(viewBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);

        return panel;
    }

    private static void createAndShowAddMovieDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Comedy Movie", true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = createMovieForm(true, dialog);
        formPanel.setPreferredSize(new Dimension(750, 600));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(780, 650));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private static JPanel createMovieForm(boolean isAddMode, JDialog parentDialog) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        formPanel.add(createFieldLabel("Movie Title:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        titleField = createStyledTextField(25);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(createFieldLabel("Running Time (min):"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 1;
        runningTimeField = createStyledTextField(8);
        formPanel.add(runningTimeField, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        JLabel ratingLabel = createFieldLabel("Comedy Rating (0-10):");
        ratingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        formPanel.add(ratingLabel, gbc);
        gbc.gridx = 3;
        ratingField = createStyledTextField(8);
        formPanel.add(ratingField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        formPanel.add(createFieldLabel("Release Date:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        releaseDateField = createStyledTextField(15);
        releaseDateField.setToolTipText("Format: YYYY-MM-DD");
        formPanel.add(releaseDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        formPanel.add(createFieldLabel("Director:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        directorField = createStyledTextField(25);
        formPanel.add(directorField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        formPanel.add(createFieldLabel("Main Actors:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        actorsField = createStyledTextField(30);
        formPanel.add(actorsField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createFieldLabel("Plot Summary:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        plotArea = createStyledTextArea(4, 30);
        JScrollPane plotScroll = new JScrollPane(plotArea);
        plotScroll.setPreferredSize(new Dimension(400, 100));
        formPanel.add(plotScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(createFieldLabel("Reviews:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        reviewsArea = createStyledTextArea(4, 30);
        JScrollPane reviewsScroll = new JScrollPane(reviewsArea);
        reviewsScroll.setPreferredSize(new Dimension(400, 100));
        formPanel.add(reviewsScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton saveBtn = createStyledButton(
                isAddMode ? "Add Movie" : "Update Movie",
                SUCCESS_COLOR
        );
        JButton clearBtn = createStyledButton("Clear Form", WARNING_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", Color.GRAY);

        saveBtn.addActionListener(e -> {
            if (isAddMode) {
                addNewMovie();
            } else {
                updateExistingMovie();
            }
            parentDialog.dispose();
        });
        clearBtn.addActionListener(e -> clearMovieForm());
        cancelBtn.addActionListener(e -> parentDialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(cancelBtn);
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(44, 62, 80));
        return label;
    }

    private static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private static JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        return area;
    }

    private static void viewSelectedMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int movieId = (int) tableModel.getValueAt(selectedRow, 0);
            Movie movie = findMovieById(movieId);
            if (movie != null) {
                showMovieDetailsDialog(movie);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a movie to view details.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void editSelectedMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int movieId = (int) tableModel.getValueAt(selectedRow, 0);
            Movie movie = findMovieById(movieId);
            if (movie != null) {
                createAndShowEditMovieDialog(movie);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a movie to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void deleteSelectedMovie() {
        int selectedRow = moviesTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirmation = JOptionPane.showConfirmDialog(mainFrame,
                    "Are you sure you want to delete this movie?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                int movieId = (int) tableModel.getValueAt(selectedRow, 0);
                movieList.removeIf(movie -> movie.getId() == movieId);
                loadMoviesIntoTable();
                JOptionPane.showMessageDialog(mainFrame,
                        "Movie deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please select a movie to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void addNewMovie() {
        try {
            Movie movie = getMovieFromForm();
            if (MovieDAO.addMovie(movie)) {
                movieList = MovieDAO.getAllMovies();
                JOptionPane.showMessageDialog(mainFrame,
                        "Movie '" + movie.getTitle() + "' added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                clearMovieForm();
                loadMoviesIntoTable();
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "Failed to add movie to the database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error adding movie: " + e.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void updateExistingMovie() {
        try {
            Movie updatedMovie = getMovieFromForm();
            for (int i = 0; i < movieList.size(); i++) {
                if (movieList.get(i).getId() == updatedMovie.getId()) {
                    movieList.set(i, updatedMovie);
                    break;
                }
            }

            JOptionPane.showMessageDialog(mainFrame,
                    "Movie updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearMovieForm();
            loadMoviesIntoTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Error updating movie: " + e.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static Movie getMovieFromForm() {
        Movie movie = new Movie();
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            throw new IllegalArgumentException("Movie title is required.");
        }
        movie.setTitle(title);
        try {
            String runningTimeText = runningTimeField.getText().trim();
            if (!runningTimeText.isEmpty()) {
                movie.setRunningTimeMinutes(Integer.parseInt(runningTimeText));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Running time must be a valid number.");
        }
        try {
            String ratingText = ratingField.getText().trim();
            if (!ratingText.isEmpty()) {
                double rating = Double.parseDouble(ratingText);
                if (rating < 0 || rating > 10) {
                    throw new IllegalArgumentException("Comedy Rating must be between 0 and 10.");
                }
                movie.setRating(rating);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Comedy Rating must be a valid number.");
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String dateText = releaseDateField.getText().trim();
            if (!dateText.isEmpty()) {
                movie.setReleaseDate(dateFormat.parse(dateText));
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Release date must be in yyyy-MM-dd format.");
        }
        movie.setDirector(new Director(directorField.getText().trim()));
        movie.setActors(Arrays.stream(actorsField.getText().split(","))
            .map(String::trim).filter(s -> !s.isEmpty()).map(Actor::new).collect(Collectors.toList()));
        movie.setPlot(plotArea.getText().trim());
        movie.setReviews(Arrays.stream(reviewsArea.getText().split(","))
            .map(String::trim).filter(s -> !s.isEmpty()).map(Review::new).collect(Collectors.toList()));
        if (editingMovieId != -1) {
            movie.setId(editingMovieId);
        }
        return movie;
    }

    private static void clearMovieForm() {
        titleField.setText("");
        runningTimeField.setText("");
        ratingField.setText("");
        releaseDateField.setText("");
        directorField.setText("");
        actorsField.setText("");
        plotArea.setText("");
        reviewsArea.setText("");
        editingMovieId = -1;
    }

    private static void populateEditForm(Movie movie) {
        editingMovieId = movie.getId();
        titleField.setText(movie.getTitle());
        runningTimeField.setText(String.valueOf(movie.getRunningTimeMinutes()));
        ratingField.setText(String.valueOf(movie.getRating()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        releaseDateField.setText(movie.getReleaseDate() != null ? dateFormat.format(movie.getReleaseDate()) : "");
        directorField.setText(movie.getDirector() != null ? movie.getDirector().getName() : "");
        actorsField.setText(movie.getActors() != null ? movie.getActors().stream().map(Actor::getName).collect(Collectors.joining(", ")) : "");
        plotArea.setText(movie.getPlot());
        reviewsArea.setText(movie.getReviews() != null ? movie.getReviews().stream().map(Review::getText).collect(Collectors.joining(", ")) : "");
    }

    private static void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Please enter a search term.",
                    "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Movie> results = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.getTitle().toLowerCase().contains(searchTerm) ||
                    movie.getDirector().getName().toLowerCase().contains(searchTerm) ||
                    movie.getActors().stream().anyMatch(actor -> actor.getName().toLowerCase().contains(searchTerm))) {
                results.add(movie);
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "No movies found matching your search criteria.",
                    "No Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder resultText = new StringBuilder();
            resultText.append("Found ").append(results.size()).append(" movie(s):\n\n");
            for (Movie movie : results) {
                resultText.append("• ").append(movie.getTitle())
                        .append(" (").append(movie.getRating()).append("/10)\n")
                        .append("  Director: ").append(movie.getDirector().getName()).append("\n")
                        .append("  Actors: ").append(movie.getActors().stream().map(Actor::getName).collect(Collectors.joining(", "))).append("\n\n");
            }

            JTextArea textArea = new JTextArea(resultText.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            JOptionPane.showMessageDialog(mainFrame, scrollPane,
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static Movie findMovieById(int id) {
        return movieList.stream()
                .filter(movie -> movie.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static void showMovieDetailsDialog(Movie movie) {
        JDialog dialog = new JDialog(mainFrame, "Movie Details: " + movie.getTitle(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel(movie.getTitle());
        titleLabel.setForeground(PRIMARY_COLOR);
        infoPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        addDetailRow(infoPanel, gbc, "Running Time:", movie.getRunningTimeMinutes() + " minutes");
        gbc.gridy = 2;
        addDetailRow(infoPanel, gbc, "Comedy Rating:", movie.getRating() + "/10");
        gbc.gridy = 3;
        addDetailRow(infoPanel, gbc, "Release Date:", movie.getReleaseDate() != null ? new SimpleDateFormat("yyyy-MM-dd").format(movie.getReleaseDate()) : "");
        gbc.gridy = 4;
        addDetailRow(infoPanel, gbc, "Director:", movie.getDirector() != null ? movie.getDirector().getName() : "");
        gbc.gridy = 5;
        addDetailRow(infoPanel, gbc, "Actors:", movie.getActors() != null ? movie.getActors().stream().map(Actor::getName).collect(Collectors.joining(", ")) : "");

        gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel plotLabel = new JLabel("Plot Summary");
        plotLabel.setForeground(PRIMARY_COLOR);
        infoPanel.add(plotLabel, gbc);

        gbc.gridy = 7;
        JTextArea plotArea = createStyledTextArea(4, 40);
        plotArea.setText(movie.getPlot());
        plotArea.setEditable(false);
        JScrollPane plotScroll = new JScrollPane(plotArea);
        plotScroll.setPreferredSize(new Dimension(400, 100));
        infoPanel.add(plotScroll, gbc);

        gbc.gridy = 8;
        JLabel reviewsLabel = new JLabel("Reviews");
        reviewsLabel.setForeground(PRIMARY_COLOR);
        infoPanel.add(reviewsLabel, gbc);

        gbc.gridy = 9;
        JTextArea reviewsArea = createStyledTextArea(4, 40);
        reviewsArea.setText(movie.getReviews() != null ? movie.getReviews().stream().map(Review::getText).collect(Collectors.joining("\n")) : "");
        reviewsArea.setEditable(false);
        JScrollPane reviewsScroll = new JScrollPane(reviewsArea);
        reviewsScroll.setPreferredSize(new Dimension(400, 100));
        infoPanel.add(reviewsScroll, gbc);

        contentPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton closeBtn = createStyledButton("Close", Color.GRAY);
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }

    private static void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        JLabel labelComp = new JLabel(label);
        labelComp.setPreferredSize(new Dimension(150, 20));
        panel.add(labelComp, gbc);

        gbc.gridx = 1;
        JLabel valueComp = new JLabel(value);
        panel.add(valueComp, gbc);
    }

    private static void createAndShowEditMovieDialog(Movie movie) {
        JDialog dialog = new JDialog(mainFrame, "Edit Movie Details", true);
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel formPanel = createMovieForm(false, dialog);
        formPanel.setPreferredSize(new Dimension(750, 600));
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setPreferredSize(new Dimension(780, 650));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        populateEditForm(movie);

        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private static void refreshStatsPanel() {
        if (dashboardTab == null || statsPanel == null) return;
        dashboardTab.remove(statsPanel);
        statsPanel = createStatsPanel();
        dashboardTab.add(statsPanel, BorderLayout.NORTH);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
}