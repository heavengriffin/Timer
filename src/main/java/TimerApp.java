import javax.naming.TimeLimitExceededException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class TimerApp extends JFrame {
    private JPanel mainPanel;
    private JRadioButton onTimeButton;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JButton chooseColorButton;
    private JComboBox speedComboBox;
    private JRadioButton countdownButton;
    private JTextField countdownTextField;
    private JButton startCountdownButton;
    private JButton stopButton;
    private JLabel colorLabel;
    private JLabel speedLabel;
    private JFormattedTextField onTimeFormattedTextField;

    TimerApp() {

        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        onTimeFormattedTextField.setValue(formattedDateTime);

        MaskFormatter maskFormatter;
        try {
            maskFormatter = new MaskFormatter("##:##:##");
            maskFormatter.install(onTimeFormattedTextField);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Timer timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalTime now = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String formattedDateTime = now.format(formatter);
                colorLabel.setText(formattedDateTime);
            }
        });
        timer.start();

        JFrame flash = new JFrame();
        flash.setSize(300, 300);
        flash.setLocation(720, 220);
        JPanel flashPanel = new JPanel();
        flash.setContentPane(flashPanel);

        final Color[] chosenColor = new Color[1];
        chooseColorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chosenColor[0] = JColorChooser.showDialog(null, "Choose your color", null);
                flashPanel.setBackground(chosenColor[0]);
                colorLabel.setForeground(chosenColor[0]);
            }
        });


        final boolean[] stopLooop = new boolean[1];
        final boolean[] stay = new boolean[1];

        startCountdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!onTimeButton.isSelected() && !countdownButton.isSelected())
                    return;

                flashPanel.setBackground(chosenColor[0]);

                timer.stop();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        stay[0] = false;
                        if (chosenColor[0] == null) {
                            JOptionPane.showMessageDialog(mainPanel, "No color selected.", "Error", JOptionPane.ERROR_MESSAGE);
                            enableComponents();
                            timer.start();
                            return;
                        }
                        if (countdownButton.isSelected()) {
                            long delay = 0;
                            if (!Objects.equals(countdownTextField.getText(), ""))
                                delay = Integer.parseInt(countdownTextField.getText());
                            else {
                                JOptionPane.showMessageDialog(mainPanel, "You haven't entered seconds for countdown.", "Error", JOptionPane.ERROR_MESSAGE);
                                enableComponents();
                                timer.start();
                                return;
                            }
                            if (delay < 0) {
                                JOptionPane.showMessageDialog(mainPanel, "Seconds cannot be negative.", "Error", JOptionPane.ERROR_MESSAGE);
                                enableComponents();
                                timer.start();
                                return;
                            }

                            else {
                                try {
                                    Thread.sleep(delay * 1000);

                                } catch (InterruptedException | IllegalArgumentException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }
                        } else if (onTimeButton.isSelected()) {
                            LocalTime now = LocalTime.now();
                            LocalTime time = null;
                            long period = 0;
                            if (onTimeFormattedTextField.getText().matches("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]")) {
                                time = LocalTime.parse(onTimeFormattedTextField.getText());
                                if (now.until(time, ChronoUnit.SECONDS) >= 0)
                                    period = now.until(time, ChronoUnit.SECONDS);
                                else {
                                    JOptionPane.showMessageDialog(mainPanel, "Specified time has already passed.", "Error", JOptionPane.ERROR_MESSAGE);
                                    enableComponents();
                                    timer.start();
                                    return;
                                }

                            }
                            else {
                                JOptionPane.showMessageDialog(mainPanel, "You have not entered the time correctly", "Error", JOptionPane.ERROR_MESSAGE);
                                enableComponents();
                                timer.start();
                                return;
                            }
                            try {
                                Thread.sleep(period * 1000);
                            } catch (InterruptedException | IllegalArgumentException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        Thread thread3 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                stopLooop[0] = false;
                                final int[] speed2 = new int[1];
                                final String[] value = new String[1];
                                String initial = (String) speedComboBox.getSelectedItem();
                                int parse = Integer.parseInt(initial);
                                speed2[0] = parse * 1000;

                                speedComboBox.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        value[0] = (String) e.getItem();
                                        int parse = Integer.parseInt(value[0]);
                                        speed2[0] = parse * 1000;
                                    }
                                });

                                while (!stopLooop[0]) {

                                    try {
                                        Thread.sleep(speed2[0]);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    if (flashPanel.getBackground().equals(chosenColor[0]))
                                        flashPanel.setBackground(Color.white);

                                    try {
                                        Thread.sleep(speed2[0]);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    if (flashPanel.getBackground().equals(Color.white))
                                        flashPanel.setBackground(chosenColor[0]);
                                }
                            }
                        });
                        thread3.start();

                        if (!stay[0])
                            flash.setVisible(true);
                    }
                });


                Thread thread2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onTimeButton.setEnabled(false);
                        onTimeFormattedTextField.setEnabled(false);
                        countdownButton.setEnabled(false);
                        countdownTextField.setEnabled(false);
                        chooseColorButton.setEnabled(false);
                        colorLabel.setEnabled(false);
                        speedLabel.setEnabled(false);
                        speedComboBox.setEnabled(false);
                        startCountdownButton.setEnabled(false);
                    }
                });
                thread2.start();

                thread.start();


            }
        });



        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
                flash.setVisible(false);
                enableComponents();
                stay[0] = true;
                stopLooop[0] = true;
            }
        });
    }

    void enableComponents() {
        onTimeButton.setEnabled(true);
        onTimeFormattedTextField.setEnabled(true);
        countdownButton.setEnabled(true);
        countdownTextField.setEnabled(true);
        chooseColorButton.setEnabled(true);
        colorLabel.setEnabled(true);
        speedLabel.setEnabled(true);
        speedComboBox.setEnabled(true);
        startCountdownButton.setEnabled(true);
    }

    public static void main(String[] args) {

        JOptionPane jOptionPane = new JOptionPane();
        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton("Close");
        buttons[1] = new JButton("Settings");

        ImageIcon questionMark = new ImageIcon("question-mark.png");
        Image questionMarkImage = questionMark.getImage();
        Image newQuestionMarkImage = questionMarkImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        Icon questionMarkIcon = new ImageIcon(newQuestionMarkImage);
        jOptionPane.setIcon(questionMarkIcon);

        jOptionPane.setOptions(buttons);
        jOptionPane.setMessage("Choose option");
        jOptionPane.isShowing();

        JDialog optionDialog = new JDialog();
        optionDialog.setContentPane(jOptionPane);
        optionDialog.setSize(250, 150);
        optionDialog.setLocationRelativeTo(null);
        optionDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        optionDialog.setTitle("Option dialog");
        optionDialog.setVisible(true);

        buttons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionDialog.dispose();
                System.exit(0);

            }
        });

        TimerApp timerAppFrame = new TimerApp();
        timerAppFrame.setBounds(200, 200, 500, 400);
        timerAppFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        timerAppFrame.setContentPane(timerAppFrame.mainPanel);

        buttons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                optionDialog.dispose();
                timerAppFrame.setVisible(true);

            }
        });


    }

}
