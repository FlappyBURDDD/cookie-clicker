import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;

public class CookieClicker extends JFrame {
    private GamePanel gamePanel;

    public CookieClicker() {
        setTitle("Cookie Clicker - Java Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        gamePanel = new GamePanel();
        add(gamePanel);
        
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CookieClicker());
    }
}

class GamePanel extends JPanel {
    private long cookies = 0;
    private double cookiesPerSecond = 0;
    private DecimalFormat df = new DecimalFormat("#,##0.00");
    
    // Upgrades
    private Upgrade cursor;
    private Upgrade grandma;
    private Upgrade farm;
    private Upgrade mine;
    private Upgrade factory;
    private Upgrade bank;
    private Upgrade wizard;
    private Upgrade portal;
    
    private Upgrade[] upgrades;
    private long lastUpdate = System.currentTimeMillis();
    
    private JButton clickButton;
    private JButton[] upgradeButtons;
    
    public GamePanel() {
        setPreferredSize(new Dimension(900, 700));
        setBackground(new Color(240, 200, 150));
        setLayout(null);
        
        // Upgrades erstellen
        cursor = new Upgrade("Cursor", 15, 0.1, new Color(200, 100, 100));
        grandma = new Upgrade("Großmutter", 100, 1, new Color(255, 150, 150));
        farm = new Upgrade("Farm", 500, 8, new Color(100, 200, 100));
        mine = new Upgrade("Mine", 2000, 40, new Color(150, 150, 150));
        factory = new Upgrade("Fabrik", 8000, 200, new Color(50, 50, 200));
        bank = new Upgrade("Bank", 30000, 1000, new Color(200, 200, 50));
        wizard = new Upgrade("Magier", 100000, 5000, new Color(200, 50, 200));
        portal = new Upgrade("Portal", 500000, 20000, new Color(100, 150, 255));
        
        upgrades = new Upgrade[]{cursor, grandma, farm, mine, factory, bank, wizard, portal};
        
        // Click Button
        clickButton = new JButton("COOKIE!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 200, 50));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(200, 150, 0));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(0, 0, getWidth(), getHeight());
                
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.drawString(text, x, y);
            }
        };
        clickButton.setBounds(50, 100, 200, 200);
        clickButton.setFocusPainted(false);
        clickButton.setContentAreaFilled(false);
        clickButton.addActionListener(e -> clickCookie());
        add(clickButton);
        
        // Upgrade Buttons
        upgradeButtons = new JButton[upgrades.length];
        for (int i = 0; i < upgrades.length; i++) {
            final int index = i;
            upgradeButtons[i] = new JButton();
            upgradeButtons[i].setBounds(350 + (i % 4) * 130, 200 + (i / 4) * 150, 120, 140);
            upgradeButtons[i].setFocusPainted(false);
            upgradeButtons[i].setFont(new Font("Arial", Font.BOLD, 11));
            upgradeButtons[i].addActionListener(e -> buyUpgrade(index));
            add(upgradeButtons[i]);
        }
        
        // Timer für automatische Cookie-Generierung
        Timer timer = new Timer(100, e -> {
            long now = System.currentTimeMillis();
            long delta = now - lastUpdate;
            cookies += (long) (cookiesPerSecond * delta / 1000.0);
            lastUpdate = now;
            repaint();
        });
        timer.start();
    }
    
    private void clickCookie() {
        cookies += 1;
        repaint();
    }
    
    private void buyUpgrade(int index) {
        Upgrade upgrade = upgrades[index];
        if (cookies >= upgrade.cost) {
            cookies -= upgrade.cost;
            upgrade.count++;
            cookiesPerSecond += upgrade.cookiesPerSecond;
            upgrade.cost = (long) (upgrade.baseCost * Math.pow(1.15, upgrade.count));
            repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Titel
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(new Color(150, 80, 0));
        g2.drawString("🍪 Cookie Clicker 🍪", 250, 50);
        
        // Cookie Count
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.setColor(new Color(100, 50, 0));
        g2.drawString("Cookies: " + formatNumber(cookies), 350, 100);
        
        // CPS (Cookies per Second)
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.setColor(new Color(100, 50, 0));
        g2.drawString("Pro Sekunde: " + df.format(cookiesPerSecond), 350, 125);
        
        // Upgrades zeichnen
        for (int i = 0; i < upgrades.length; i++) {
            Upgrade upgrade = upgrades[i];
            JButton btn = upgradeButtons[i];
            
            // Button-Hintergrund
            boolean canAfford = cookies >= upgrade.cost;
            if (canAfford) {
                btn.setBackground(new Color(100, 255, 100));
            } else {
                btn.setBackground(new Color(200, 200, 200));
            }
            btn.setOpaque(true);
            btn.setBorder(new javax.swing.border.LineBorder(upgrade.color, 3));
            
            // Text auf Button
            String text = "<html><center><b>" + upgrade.name + "</b><br>" +
                         "x" + upgrade.count + "<br>" +
                         formatNumber(upgrade.cost) + " 🍪<br>" +
                         "+" + df.format(upgrade.cookiesPerSecond) + "/s</center></html>";
            btn.setText(text);
        }
    }
    
    private String formatNumber(long num) {
        if (num < 1000) return String.valueOf(num);
        if (num < 1_000_000) return String.format("%.1fK", num / 1000.0);
        if (num < 1_000_000_000L) return String.format("%.1fM", num / 1_000_000.0);
        return String.format("%.1fB", num / 1_000_000_000.0);
    }
}

class Upgrade {
    String name;
    long baseCost;
    long cost;
    double cookiesPerSecond;
    int count;
    Color color;
    
    public Upgrade(String name, long baseCost, double cookiesPerSecond, Color color) {
        this.name = name;
        this.baseCost = baseCost;
        this.cost = baseCost;
        this.cookiesPerSecond = cookiesPerSecond;
        this.count = 0;
        this.color = color;
    }
}