import java.util.Stack;

public class ExpressionParser {
    private Stack<Double> numberStack = new Stack<>(); // 存储数字的栈
    private Stack<Character> operatorStack = new Stack<>(); // 存储运算符的栈

    public double evaluate(String expression) {


        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (ch == '(') { // 如果是左括号
                operatorStack.push(ch); // 将左括号入栈
            } else if (Character.isDigit(ch) || ch == '.') { // 如果是数字或小数点
                StringBuilder number = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i)); // 将连续的数字字符或小数点添加到字符串中
                    i++;
                }
                i--;
                double num = Double.parseDouble(number.toString()); // 将字符串转换为双精度浮点数
                numberStack.push(num); // 将数字入栈
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') { // 如果是加号、减号、乘号或除号
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(' && hasPrecedence(ch, operatorStack.peek())) {
                    performOperation(); // 执行操作，直到栈顶运算符优先级低于当前运算符
                }
                operatorStack.push(ch); // 将当前运算符入栈
            } else if (ch == ')') { // 如果是右括号
                if (operatorStack.isEmpty()) {
                    throw new IllegalArgumentException("算式语法错误"); // 如果栈为空，表示括号不匹配，抛出异常
                }

                while (operatorStack.peek() != '(') {
                    performOperation(); // 执行操作，直到遇到左括号
                    if (operatorStack.isEmpty()) {
                        throw new IllegalArgumentException("算式语法错误"); // 如果栈为空，表示括号不匹配，抛出异常
                    }
                }

                operatorStack.pop(); // 移除左括号
            }
        }

        while (!operatorStack.isEmpty()) {
            performOperation(); // 执行剩余的操作
        }

        if (numberStack.isEmpty()) {
            throw new IllegalArgumentException("算式语法错误"); // 如果数字栈为空，表示算式有误，抛出异常
        }

        return numberStack.pop(); // 返回最终计算结果
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) {
            return false; // 如果op1是乘号或除号，op2是加号或减号，则op1优先级高于op2
        }
        return true; // 否则，op1优先级低于op2
    }

    private void performOperation() {
        if (numberStack.size() < 2) {
            throw new IllegalArgumentException("算式语法错误"); // 如果数字栈中的元素少于2个，表示算式有误，抛出异常
        }
        double num2 = numberStack.pop(); // 弹出数字栈顶元素作为操作数2
        double num1 = numberStack.pop(); // 弹出数字栈顶元素作为操作数1
        char operator = operatorStack.pop(); // 弹出运算符栈顶元素作为操作符
        double result = 0.0; // 存储计算结果
        switch (operator) {
            case '+':
                result = num1 + num2; // 执行加法运算
                break;
            case '-':
                result = num1 - num2; // 执行减法运算
                break;
            case '*':
                result = num1 * num2; // 执行乘法运算
                break;
            case '/':
                if (num2 == 0) {
                    throw new IllegalArgumentException("除数不能为零"); // 如果除数为零，抛出异常
                }
                result = num1 / num2; // 执行除法运算
                break;
        }
        numberStack.push(result); // 将计算结果入栈
    }
}
