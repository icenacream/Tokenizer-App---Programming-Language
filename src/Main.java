import javax.swing.*;
//import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class Main extends JFrame {

    //  Palette 
    private static final Color BG_DARK      = new Color(0x13141f);
    private static final Color BG_PANEL     = new Color(0x1a1b2e);
    private static final Color BG_INPUT     = new Color(0x1e2030);
    private static final Color BORDER_COL   = new Color(0x2d3154);
    private static final Color BORDER_FOCUS = new Color(0x7c5af0);
    private static final Color TEXT_MAIN    = new Color(0xf1f5f9);
    private static final Color TEXT_MUTED   = new Color(0x94a3b8);

    private static final Color C_KEYWORD    = new Color(0x60a5fa);
    private static final Color C_IDENTIFIER = new Color(0x4ade80);
    private static final Color C_OPERATOR   = new Color(0xfb923c);
    private static final Color C_INT        = new Color(0xa78bfa);
    private static final Color C_FLOAT      = new Color(0xc084fc);
    private static final Color C_STRING     = new Color(0xe879f9);
    private static final Color C_CHAR       = new Color(0xf0abfc);
    private static final Color C_BOOLEAN    = new Color(0x818cf8);
    private static final Color C_NULL       = new Color(0x6366f1);
    private static final Color C_DELIMITER  = new Color(0xf472b6);
    private static final Color C_UNKNOWN    = new Color(0x94a3b8);

    private JTextArea inputArea;
    private JPanel    tokensGrid;
    private JLabel    tokensTitle;
    private JPanel    resultsPanel;

    public Main() {
        setTitle("Lexer Tokenizer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 700);
        setMinimumSize(new Dimension(720, 520));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout());
        add(buildMainPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    //  Outer scroll wrapper 
    private JScrollPane buildMainPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_DARK);
        root.setBorder(BorderFactory.createEmptyBorder(36, 40, 40, 40));

        root.add(buildHeader());
        root.add(vgap(28));
        root.add(buildInputSection());
        root.add(vgap(18));
        root.add(buildTokenizeButton());
        root.add(vgap(24));

        resultsPanel = buildResultsPanel();
        resultsPanel.setVisible(false);
        root.add(resultsPanel);
        root.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(root);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG_DARK);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    //  Header 
    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(BG_DARK);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JPanel logo = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(0x7c5af0), getWidth(), getHeight(), new Color(0x5b8af5)));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Monospaced", Font.BOLD, 22));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "</>";
                g2.drawString(txt, (getWidth() - fm.stringWidth(txt)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        logo.setPreferredSize(new Dimension(60, 60));
        logo.setOpaque(false);

        JLabel title = new JLabel("Lexer Tokenizer");
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(TEXT_MAIN);
        title.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        p.add(logo);
        p.add(title);
        return p;
    }

    //  Input section 
    private JPanel buildInputSection() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG_DARK);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        JLabel label = new JLabel("Enter your code here");
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 8, 0));

        inputArea = new JTextArea(6, 40);
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        inputArea.setBackground(BG_INPUT);
        inputArea.setForeground(TEXT_MAIN);
        inputArea.setCaretColor(new Color(0xa78bfa));
        inputArea.setSelectionColor(new Color(0x7c5af0, false));
        inputArea.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        // Enter = tokenize, Shift+Enter = newline
        inputArea.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    runTokenizer();
                }
            }
        });

        // Custom scroll pane that draws its own rounded border + background
        JScrollPane inputScroll = new JScrollPane(inputArea) {
            private boolean focused = false;
            {
                inputArea.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) { focused = true;  repaint(); }
                    @Override public void focusLost (FocusEvent e) { focused = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(focused ? BORDER_FOCUS : BORDER_COL);
                g2.setStroke(new BasicStroke(1.8f));
                g2.draw(new RoundRectangle2D.Float(0.9f, 0.9f, getWidth()-1.8f, getHeight()-1.8f, 14, 14));
                g2.dispose();
            }
        };
        inputScroll.setBorder(null);
        inputScroll.setOpaque(false);
        inputScroll.getViewport().setOpaque(false);
        inputScroll.getViewport().setBackground(BG_INPUT);
        inputScroll.setAlignmentX(LEFT_ALIGNMENT);
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        p.add(label);
        p.add(inputScroll);
        return p;
    }

    //  Tokenize button 
    private JPanel buildTokenizeButton() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(BG_DARK);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JButton btn = new JButton("Tokenize") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float alpha = hovered ? 0.85f : 1.0f;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.setPaint(new GradientPaint(0, 0, new Color(0x5b8af5), getWidth(), 0, new Color(0x7c5af0)));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setComposite(AlphaComposite.SrcOver);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth()  - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setPreferredSize(new Dimension(160, 46));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> runTokenizer());

        p.add(btn);
        return p;
    }

    //  Results panel 
    private JPanel buildResultsPanel() {
        // Outer card — paints its own rounded background
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_PANEL);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(BORDER_COL);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 22, 22, 22));
        card.setAlignmentX(LEFT_ALIGNMENT);

        //  Top bar: title left, legend right 
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        tokensTitle = new JLabel("Tokens (0)");
        tokensTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        tokensTitle.setForeground(TEXT_MAIN);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        legend.setOpaque(false);
        String[] lNames  = { "Keyword", "Identifier", "Operator", "Int", "Float", "String", "Char", "Boolean", "Null", "Delimiter" };
        Color[]  lColors = { C_KEYWORD, C_IDENTIFIER, C_OPERATOR, C_INT, C_FLOAT, C_STRING, C_CHAR, C_BOOLEAN, C_NULL, C_DELIMITER };
        for (int i = 0; i < lNames.length; i++) legend.add(legendChip(lNames[i], lColors[i]));

        topBar.add(tokensTitle, BorderLayout.WEST);
        topBar.add(legend,      BorderLayout.EAST);

        //  Token cards wrap panel 
        tokensGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        tokensGrid.setOpaque(false);

        card.add(topBar,      BorderLayout.NORTH);
        card.add(tokensGrid,  BorderLayout.CENTER);
        return card;
    }

    //  Legend chip 
    private JLabel legendChip(String text, Color color) {
        JLabel chip = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 28));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.4f));
                g2.draw(new RoundRectangle2D.Float(0.7f, 0.7f, getWidth()-1.4f, getHeight()-1.4f, getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(new Font("SansSerif", Font.BOLD, 11));
        chip.setForeground(color);
        chip.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        chip.setOpaque(false);
        return chip;
    }

    //  Run tokenizer 
    private void runTokenizer() {
        String input = inputArea.getText().trim();
        tokensGrid.removeAll();

        if (input.isEmpty()) {
            resultsPanel.setVisible(false);
            return;
        }

        Lexer lexer = new Lexer();
        List<Token> tokens = lexer.tokenize(input);

        tokensTitle.setText("Tokens (" + tokens.size() + ")");
        for (Token t : tokens) tokensGrid.add(buildTokenCard(t));

        resultsPanel.setVisible(true);
        tokensGrid.revalidate();
        tokensGrid.repaint();
        resultsPanel.revalidate();
        resultsPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, tokensGrid);
            if (sp != null) sp.getVerticalScrollBar().setValue(sp.getVerticalScrollBar().getMaximum());
        });
    }

    //  Token card 
    private JPanel buildTokenCard(Token token) {
        Color color   = colorFor(token.getType());
        Color bgColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 35);
        Color bdColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 110);

        JLabel valueLabel = new JLabel(token.getValue(), SwingConstants.CENTER);
        valueLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        valueLabel.setForeground(color);

        JLabel typeLabel = new JLabel(token.getType().name(), SwingConstants.CENTER);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 9));
        typeLabel.setForeground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 190));

        // Compute card width dynamically based on text
        FontMetrics fmVal  = getFontMetrics(valueLabel.getFont());
        FontMetrics fmType = getFontMetrics(typeLabel.getFont());
        int minW = Math.max(fmVal.stringWidth(token.getValue()), fmType.stringWidth(token.getType().name())) + 36;
        int cardW = Math.max(minW, 76);

        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(bdColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        card.setPreferredSize(new Dimension(cardW, 68));

        valueLabel.setAlignmentX(CENTER_ALIGNMENT);
        typeLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(typeLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    //  Helpers 
    private Color colorFor(TokenType type) {
        return switch (type) {
            case KEYWORD         -> C_KEYWORD;
            case IDENTIFIER      -> C_IDENTIFIER;
            case OPERATOR        -> C_OPERATOR;
            case INT_LITERAL     -> C_INT;
            case FLOAT_LITERAL   -> C_FLOAT;
            case STRING_LITERAL  -> C_STRING;
            case CHAR_LITERAL    -> C_CHAR;
            case BOOLEAN_LITERAL -> C_BOOLEAN;
            case NULL_LITERAL    -> C_NULL;
            case DELIMITER       -> C_DELIMITER;
            default              -> C_UNKNOWN;
        };
    }

    private static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    //  WrapLayout 
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

        @Override public Dimension preferredLayoutSize(Container t) { return layout(t, true);  }
        @Override public Dimension minimumLayoutSize (Container t) { return layout(t, false); }

        private Dimension layout(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int w = target.getSize().width;
                if (w == 0) w = Integer.MAX_VALUE;
                Insets ins = target.getInsets();
                int maxW = w - ins.left - ins.right - getHgap() * 2;
                int x = 0, y = ins.top + getVgap(), rowH = 0;
                for (Component c : target.getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                    if (x > 0 && x + d.width > maxW) { y += rowH + getVgap(); x = 0; rowH = 0; }
                    x += d.width + getHgap();
                    rowH = Math.max(rowH, d.height);
                }
                return new Dimension(maxW, y + rowH + getVgap() + ins.bottom);
            }
        }
    }

    //  Entry point 
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException ignored) {}
        SwingUtilities.invokeLater(Main::new);
    }
}