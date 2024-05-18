package com.hahaha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BallBounceGame extends JFrame {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BALL_SIZE = 50;
    private static final int BLOCK_SIZE = 30;
    private List<Ball> balls;
    private Rectangle mouseBlock;
    private Timer timer;
    private long startTime;
    private JLabel timeLabel;
    private JPanel panel;
    private int ballCount;
    private int score;

    public BallBounceGame() {
        //球列表
        balls = new ArrayList<>();

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Ball ball : balls) {
                    ball.draw(g);
                }
                g.fillRect(mouseBlock.x, mouseBlock.y, mouseBlock.width, mouseBlock.height);
            }
        };

        //每十毫秒update一次（更新游戏状态和重绘游戏窗口）
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        //计时器
        timeLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        //初始化游戏窗口
        initializeGame();
    }

    //处理鼠标事件
    class MouseHandler extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            // 更新鼠标方块的位置
            int x = e.getX() - mouseBlock.width / 2;
            int y = e.getY() - mouseBlock.height / 2;
            mouseBlock.setLocation(x, y);
        }
    }

    private void initializeGame() {
        setTitle("Ball Bounce Game made by ljr");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(panel, BorderLayout.CENTER);
        add(timeLabel, BorderLayout.SOUTH);
        setVisible(true);
        mouseBlock = new Rectangle(0, 0, BLOCK_SIZE, BLOCK_SIZE);
        // 设置球的数量
        String input = JOptionPane.showInputDialog(this, "Enter number of balls:");
        if (input != null) {
            ballCount = Integer.parseInt(input);
            resetBalls();
        }
        // 添加鼠标事件监听器
        panel.addMouseMotionListener(new MouseHandler());
        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

    private void resetBalls() {
        balls.clear();
        Random random = new Random();
        for (int i = 0; i < ballCount; i++) {
            int x = random.nextInt(WINDOW_WIDTH - 2 * BALL_SIZE) + BALL_SIZE;
            int y = random.nextInt(WINDOW_HEIGHT - 2 * BALL_SIZE) + BALL_SIZE;
            double vx = random.nextDouble() * 2 - 1;
            double vy = random.nextDouble() * 2 - 1;
            balls.add(new Ball(x, y, vx, vy,new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256)));
        }
        startTime = System.currentTimeMillis();
        timeLabel.setText("Time: 0s");
        score = 0;
        //启动定时任务
        timer.start();
    }

    private void update() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        timeLabel.setText("Time: " + (elapsedTime / 1000) + "s");

        for (Ball ball : balls) {
            //更新球的位置
            ball.update();
            //判断鼠标方块是否与小球/窗口四周碰撞
            if(mouseBlock.intersects(ball.x, ball.y, ball.size*0.5, ball.size*0.5) || isOutOfBounds(mouseBlock.x, mouseBlock.y, BLOCK_SIZE/2)) {
                endGame();
                break;
            }
            //判断小球是否出界
            if (isOutOfBounds(ball.x, ball.y, ball.size)) {
                ball.bounce();
            }
        }
        // 更新得分
        score = (int) (System.currentTimeMillis() - startTime) / 1000;

        panel.repaint();
    }

    //判断出界
    public boolean isOutOfBounds(int x, int y, int size) {
        return x < 0 || x > WINDOW_WIDTH - size || y < 0 || y > WINDOW_HEIGHT - size;
    }

    // 结束游戏
    private void endGame() {
        timer.stop();
        int result = JOptionPane.showConfirmDialog(this,
                "Game Over! Your score is: " + score + "\n" +
                        "Do you want to restart or exit(Y:restart;N:exit)?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            initializeGame();
        } else {
            System.exit(0); // 或者可以选择关闭窗口而不是退出整个程序
        }
    }

    //球类
    static class Ball {
        private int x, y;
        private double vx, vy;
        private final int size;
        private final int color1, color2, color3;

        public Ball(int x, int y, double vx, double vy, int color1, int color2, int color3) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
            this.size = BALL_SIZE;
        }

        //画球
        public void draw(Graphics g) {
            g.setColor(new Color(color1, color2, color3));
            g.fillOval(x, y, size, size);
        }
        //更新球坐标
        public void update() {
            x += (int) (vx * 4);
            y += (int) (vy * 4);
        }

        //球出界后反弹
        public void bounce() {
            vx *= -1;
            vy *= -1;
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BallBounceGame();
            }
        });
    }
}
