// Package
package MainClasses;

// Imports
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Rectangle extends JPanel {
    private int length;
    private int height;
    Image pfp;
    private static String defaultImagePath = "public/defaultPfp.jpg";

    public Rectangle(int length, int height) {
        this.length = length;
        this.height = height;
        setPreferredSize(new Dimension(length, height));
        setMinimumSize(new Dimension(length, height));

        // Checks to make sure image is there
        File file = new File(defaultImagePath);
        if (!file.exists() || file.isDirectory() || !file.canRead())
            defaultImagePath = null;
    }

    public void setImage(Image image) {
        this.pfp = image;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, length, height);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, length - 1, height - 1);

        if (this.pfp != null) {
            g.drawImage(this.pfp, 0, 0, this.length, this.height, this);
        } else if (defaultImagePath != null) {
            Image defaultImage = Toolkit.getDefaultToolkit().getImage(defaultImagePath);
            g.drawImage(defaultImage, 0, 0, this.length, this.height, this);
        }

    }
}