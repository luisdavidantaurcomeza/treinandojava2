import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculadora {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Calculadora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);

        // Criar um painel para o visor
        JPanel panelVisor = new JPanel();
        JTextField visor = new JTextField(16);
        visor.setEditable(false);
        visor.setHorizontalAlignment(JTextField.RIGHT);
        panelVisor.add(visor);

        // Criar um painel para os botões
        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new GridLayout(4, 4));

        // Botões da calculadora
        String[] botoes = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"
        };

        // Adicionar botões ao painel
        for (String texto : botoes) {
            JButton botao = new JButton(texto);
            panelBotoes.add(botao);

            // Adicionar ação aos botões
            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String comando = e.getActionCommand();

                    if (comando.equals("=")) {
                        visor.setText(evaluar(visor.getText()));
                    } else {
                        visor.setText(visor.getText() + comando);
                    }
                }
            });
        }

        // Adicionar os painéis ao frame
        frame.add(panelVisor, BorderLayout.NORTH);
        frame.add(panelBotoes, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    // Método para avaliar a expressão matemática
    public static String evaluar(String expressao) {
        try {
            return Double.toString(eval(expressao));
        } catch (Exception e) {
            return "Erro";
        }
    }

    // Método de avaliação da expressão usando JavaScript engine
    public static double eval(final String str) {
        class Parser {
            int pos = -1, c;

            void eatChar() {
                c = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            void eatSpace() {
                while (Character.isWhitespace(c)) eatChar();
            }

            double parse() {
                eatChar();
                double v = parseExpression();
                if (c != -1) throw new RuntimeException("Caractere inesperado: " + (char)c);
                return v;
            }

            double parseExpression() {
                double v = parseTerm();
                for (;;) {
                    eatSpace();
                    if (c == '+') { // Adição
                        eatChar();
                        v += parseTerm();
                    } else if (c == '-') { // Subtração
                        eatChar();
                        v -= parseTerm();
                    } else {
                        return v;
                    }
                }
            }

            double parseTerm() {
                double v = parseFactor();
                for (;;) {
                    eatSpace();
                    if (c == '/') { // Divisão
                        eatChar();
                        v /= parseFactor();
                    } else if (c == '*') { // Multiplicação
                        eatChar();
                        v *= parseFactor();
                    } else {
                        return v;
                    }
                }
            }

            double parseFactor() {
                double v;
                boolean negate = false;
                eatSpace();
                if (c == '+' || c == '-') { // Unário
                    negate = c == '-';
                    eatChar();
                    eatSpace();
                }
                if (c == '(') { // Parênteses
                    eatChar();
                    v = parseExpression();
                    if (c == ')') eatChar();
                } else { // Números
                    StringBuilder sb = new StringBuilder();
                    while ((c >= '0' && c <= '9') || c == '.') { sb.append((char)c); eatChar(); }
                    if (sb.length() == 0) throw new RuntimeException("Número esperado");
                    v = Double.parseDouble(sb.toString());
                }
                eatSpace();
                if (negate) v = -v;
                return v;
            }
        }
        return new Parser().parse();
    }
}
