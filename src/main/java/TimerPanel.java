import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    private JRadioButton onTime = new JRadioButton("on time: ");
    private JRadioButton countdown = new JRadioButton("countdown (sec): ");
    ButtonGroup options = new ButtonGroup();

    public void setOptions(ButtonGroup options) {
        options.add(onTime);
        options.add(countdown);
    }


    public TimerPanel() {
        options.add(onTime);
        options.add(countdown);
        //setOptions(options);
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(onTime, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(countdown, gridBagConstraints);

    }
}
