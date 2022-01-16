package auctionsniper.ui;

import auctionsniper.Main;

import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        super("Auction sniper");
        setName(Main.MAIN_WINDOW_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
