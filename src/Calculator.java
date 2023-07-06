import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator extends JFrame implements ActionListener {
    private JPanel jp_north = new JPanel();
    private JTextField input_text = new JTextField();
    private JButton c_Btn = new JButton("C");
    private JButton ce_Btn = new JButton("CE");
    private JPanel jp_center = new JPanel();

    private Stack<String> inputStack = new Stack<>();
    private boolean errorFlag = false;

    private List<String> calculationHistory = new ArrayList<>();

    public Calculator() throws HeadlessException {
        this.init();
        this.addNorthComponent();
        this.addCenterButton();
    }

    public void init() {
        this.setTitle("计算器");
        this.setSize(300, 300);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void addNorthComponent() {
        this.input_text.setPreferredSize(new Dimension(150, 30));
        jp_north.add(input_text);
        this.c_Btn.setBackground(Color.RED);
        this.ce_Btn.setBackground(Color.GREEN);
        jp_north.add(c_Btn);
        jp_north.add(ce_Btn);

        // "清除最后输入"按钮的监听器
        c_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLastInput();
            }
        });

        // "清除所有输入"按钮的监听器
        ce_Btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });

        this.add(jp_north, BorderLayout.NORTH);
    }

    public void addCenterButton() {
        String btn_text = "123+456-789*0.=()/";
        this.jp_center.setLayout(new GridLayout(5, 4));

        // 循环创建按钮并添加到中间面板
        for (int i = 0; i < btn_text.length(); i++) {
            String temp = btn_text.substring(i, i + 1);
            JButton btn = new JButton();
            btn.setText(temp);
            if (temp.equals("+") || temp.equals("-") || temp.equals("*") || temp.equals("/") || temp.equals(".")) {
                btn.setFont(new Font("粗体", Font.BOLD, 16));
                btn.setForeground(Color.RED);
            }
            btn.addActionListener(this);
            jp_center.add(btn);
        }

        this.add(jp_center, BorderLayout.CENTER);
    }

    private void clearLastInput() {
        if (!inputStack.isEmpty()) {
            inputStack.pop();
            updateInputText();
        }
    }

    private void clearAll() {
        inputStack.clear();
        input_text.setText("");
        errorFlag = false;
    }

    private void updateInputText() {
        StringBuilder inputExpression = new StringBuilder();
        for (String str : inputStack) {
            inputExpression.append(str);
        }
        input_text.setText(inputExpression.toString());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
        errorFlag = true;
        clearAll();
    }

    private void saveToLogFile(String expression) {
        calculationHistory.add(expression);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);
        String filePath = "calculation_history.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            String line = "------------------------";
            writer.newLine();
            writer.write(line);
            writer.newLine();
            writer.write("Time: " + currentTime);
            writer.newLine();
            writer.write("Formula: " + expression);
            writer.newLine();
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (errorFlag) {
            // 如果发生错误，点击按钮后清除错误状态
            clearAll();
        }

        String clickStr = e.getActionCommand();
        if (clickStr.matches("[0-9.]")) {
            inputStack.push(clickStr);
            updateInputText();
        } else if (clickStr.matches("[+\\-*/()]")) {
            inputStack.push(clickStr);
            updateInputText();
        } else if (clickStr.equals("=")) {
            String expression = input_text.getText();
            try {
                double result = evaluateExpression(expression);
                input_text.setText(String.valueOf(result));
                saveToLogFile(expression + " = " + result);
                inputStack.clear();
                inputStack.push(String.valueOf(result));
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());










































































            }
        }
    }

    private double evaluateExpression(String expression) {
        ExpressionParser parser = new ExpressionParser();
        return parser.evaluate(expression);
    }
}
