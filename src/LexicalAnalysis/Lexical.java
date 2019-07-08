/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LexicalAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author PepperMint
 */
public class Lexical {

    private int line;
    private int pos;
    private int position;
    private char chr;
    private String s;

    Map<String, TokenType> keywords = new HashMap<>();

    static class Token {

        public TokenType tokentype;
        public String value;
        public int line;
        public int pos;

        Token(TokenType token, String value, int line, int pos) {
            this.tokentype = token;
            this.value = value;
            this.line = line;
            this.pos = pos;
        }

        @Override
        public String toString() {
            String result = String.format("%5d                  %5d                  %-20s", this.line, this.pos, this.tokentype);
            switch (this.tokentype) {
                case Integer:
                    result += String.format("                    %4s", value);
                    break;
                case Định_danh:
                    result += String.format("                    %s", value);
                    break;
                case Chuỗi:
                    result += String.format("                    \"%s\"", value);
                    break;
                default:
                    result += String.format("                    %s", value);
                    break;
            }
            return result;
        }
    }

    static enum TokenType {

        End_of_input, Toán_tử_nhân, Chia_lấy_nguyên, Chia_lấy_dư, Toán_tử_cộng, Toán_tử_trừ,
        Op_negate, Phủ_định, So_sánh_nhỏ_hơn, Nhỏ_hơn_hoặc_bằng, So_sánh_lớn_hơn, Lớn_hơn_hoặc_bằng,
        So_sánh_bằng, So_sánh_khác, Toán_tử_gán, And_logic, Or_logic, And_gán, Or_gán, Từ_khóa, Mở_ngoặc_tròn, Đóng_ngoặc_tròn,
        Mở_ngoặc_nhọn, Đóng_ngoặc_nhọn, Dấu_chấm_phẩy, Dấu_phẩy, Định_danh, Integer, Chuỗi, And_bit,
        Or_bit, Xor_bit, Dịch_trái, Dịch_phải, Chú_thích_dòng, Chú_thích_đoạn, Xor_gán, Tăng_1, Giảm_1,
        Cộng_gán, Trừ_gán, Chia_gán, Nhân_gán, Đảo_bit, Float
    }

    static void error(int line, int pos, String msg) {
        if (line > 0 && pos > 0) {
            System.out.printf("%s in line %d, pos %d\n", msg, line, pos);
        } else {
            System.out.println(msg);
        }
        System.exit(1);
    }

    Lexical(String source) {
        this.line = 1;
        this.pos = 0;
        this.position = 0;
        this.s = source;
        this.chr = this.s.charAt(0);
        this.keywords.put("asm", TokenType.Từ_khóa);
        this.keywords.put("auto", TokenType.Từ_khóa);
        this.keywords.put("break", TokenType.Từ_khóa);
        this.keywords.put("case", TokenType.Từ_khóa);
        this.keywords.put("char", TokenType.Từ_khóa);
        this.keywords.put("const", TokenType.Từ_khóa);
        this.keywords.put("continue", TokenType.Từ_khóa);
        this.keywords.put("default", TokenType.Từ_khóa);
        this.keywords.put("delete", TokenType.Từ_khóa);
        this.keywords.put("do", TokenType.Từ_khóa);
        this.keywords.put("double", TokenType.Từ_khóa);
        this.keywords.put("else", TokenType.Từ_khóa);
        this.keywords.put("enum", TokenType.Từ_khóa);
        this.keywords.put("exterm", TokenType.Từ_khóa);
        this.keywords.put("far", TokenType.Từ_khóa);
        this.keywords.put("float", TokenType.Từ_khóa);
        this.keywords.put("for", TokenType.Từ_khóa);
        this.keywords.put("friend", TokenType.Từ_khóa);
        this.keywords.put("goto", TokenType.Từ_khóa);
        this.keywords.put("if", TokenType.Từ_khóa);
        this.keywords.put("int", TokenType.Từ_khóa);
        this.keywords.put("interrupt", TokenType.Từ_khóa);
        this.keywords.put("long", TokenType.Từ_khóa);
        this.keywords.put("near", TokenType.Từ_khóa);
        this.keywords.put("new", TokenType.Từ_khóa);
        this.keywords.put("pascal", TokenType.Từ_khóa);
        this.keywords.put("register", TokenType.Từ_khóa);
        this.keywords.put("return", TokenType.Từ_khóa);
        this.keywords.put("_seg", TokenType.Từ_khóa);
        this.keywords.put("short", TokenType.Từ_khóa);
        this.keywords.put("struct", TokenType.Từ_khóa);
        this.keywords.put("switch", TokenType.Từ_khóa);
        this.keywords.put("this", TokenType.Từ_khóa);
        this.keywords.put("union", TokenType.Từ_khóa);
        this.keywords.put("unsigned", TokenType.Từ_khóa);
        this.keywords.put("void", TokenType.Từ_khóa);
        this.keywords.put("volatile", TokenType.Từ_khóa);
        this.keywords.put("while", TokenType.Từ_khóa);
        this.keywords.put("signed", TokenType.Từ_khóa);
        this.keywords.put("switch", TokenType.Từ_khóa);
        this.keywords.put("typedef", TokenType.Từ_khóa);
        this.keywords.put("typename", TokenType.Từ_khóa);
        this.keywords.put("throw", TokenType.Từ_khóa);
        this.keywords.put("sizeof", TokenType.Từ_khóa);

    }

    Token follow(char expect, char expect1, char expect2, TokenType ifyes, TokenType ifno, int line, int pos) {
        String q1 = Character.toString(expect);
        String q2 = Character.toString(expect1);
        String q3 = Character.toString(expect2);
        if (getNextChar() == expect) {
            getNextChar();
            return new Token(ifyes, q1+q3, line, pos);
        }
        if (ifno == TokenType.End_of_input) {
            error(line, pos, String.format("follow: unrecognized character: (%d) '%c'", (int) this.chr, this.chr));
        }
        return new Token(ifno, q2, line, pos);
    }

    Token follow1(char expect1, char expect2, TokenType ifyes, TokenType ifno, TokenType ifno1, int line, int pos) {
        if (getNextChar() == expect1 && getNextChar() != expect2) {
            getNextChar();
            return new Token(ifyes, "", line, pos);
        }
        if (getNextChar() == expect1 && getNextChar() != expect2) {
            getNextChar();
            return new Token(ifno, "", line, pos);
        }
        return new Token(ifno1, "", line, pos);
    }

    Token char_lit(int line, int pos) {
        char c = getNextChar(); // skip opening quote
        int n = (int) c;
        if (c == '\'') {
            error(line, pos, "empty character constant");
        } else if (c == '\\') {
            c = getNextChar();
            if (c == 'n') {
                n = 10;
            } else if (c == '\\') {
                n = '\\';
            } else {
                error(line, pos, String.format("unknown escape sequence \\%c", c));
            }
        }
        if (getNextChar() != '\'') {
            error(line, pos, "multi-character constant");
        }
        getNextChar();
        return new Token(TokenType.Integer, "" + n, line, pos);
    }

    Token string_lit(char start, int line, int pos) {
        String result = "";
        while (getNextChar() != start) {
            if (this.chr == '\u0000') {
                error(line, pos, "EOF while scanning string literal");
            }
            if (this.chr == '\n') {
                error(line, pos, "EOL while scanning string literal");
            }
            result += this.chr;
        }
        getNextChar();
        return new Token(TokenType.Chuỗi, result, line, pos);
    }

    Token div_or_comment(int line, int pos) {
        if (getNextChar() != '*') {
            return new Token(TokenType.Chia_lấy_nguyên, "", line, pos);
        }
        getNextChar();
        while (true) {
            if (this.chr == '\u0000') {
                error(line, pos, "EOF in comment");
            } else if (this.chr == '*') {
                if (getNextChar() == '/') {
                    getNextChar();
                    return getToken();
                }
            } else {
                getNextChar();
            }
        }
    }

    Token identifier_or_integer(int line, int pos) {
        boolean is_number = true;
        String text = "";

        while (Character.isAlphabetic(this.chr) || Character.isDigit(this.chr) || this.chr == '_' || this.chr == '.' || this.chr == 'e' || this.chr == 'E') {
            text += this.chr;
            if (!Character.isDigit(this.chr)) {
                is_number = false;
                if(!Character.isDigit(this.chr) && (this.chr == '.'|| this.chr == 'e' || this.chr == 'E')) is_number =true;
            }
            getNextChar();
        }

        if (text.equals("")) {
            error(line, pos, String.format("identifer_or_integer unrecopgnized character: (%d) %c",  this.chr, this.chr));
        }

        if (Character.isDigit(text.charAt(0))) {
            if (!is_number && !text.contains("E") && !text.contains("e")) {
                error(line, pos, String.format("invaslid number: %s", text));
            }
            if(text.contains(".") || text.contains("E") || text.contains("e")) {return new Token(TokenType.Float, text, line, pos); };
            return new Token(TokenType.Integer, text, line, pos);
        }

        if (this.keywords.containsKey(text)) {
            return new Token(this.keywords.get(text), text, line, pos);
        }
        return new Token(TokenType.Định_danh, text, line, pos);
    }

    Token getToken() {
        int line, pos;
        while (Character.isWhitespace(this.chr)) {
            getNextChar();
        }
        line = this.line;
        pos = this.pos;

        switch (this.chr) {
            case '\u0000':
                return new Token(TokenType.End_of_input, "", this.line, this.pos);
            case '/':
                char c = getNextChar();
                if (c == '/') {
                    String t = "";
                    char d;
                    while (line == this.line) {
                        //d = getNextChar();
                        t += Character.toString(getNextChar());
                    }
                    //getNextLine();
                    return new Token(TokenType.Chú_thích_dòng, "//" + t, line, pos);
                }
                if (c == '*') {
                    String t1 = "";
                    char d1;
                    // getNextChar(); 
                    while (getNextChar() != '*' && get2Char() != '/') {
                        //d1 = getNextChar();
                        //t1 += Character.toString(getNextChar());
                        getNextChar(); 
                    }
                    //if (getNextChar() != '*' && get2Char() != '/') {
                        getNextChar();
                        // t1+=Character.toString(d1);
                        line = this.line++;
                        return new Token(TokenType.Chú_thích_đoạn, "", line - 1, pos);
                    //}

                }
                if(c == '=') {
                    getNextChar();
                    return new Token(TokenType.Chia_gán, "/=", line, pos);
                }
                return new Token(TokenType.Chia_lấy_nguyên, "/", line, pos);//return div_or_comment(line, pos);
            case '\'':
                return char_lit(line, pos);
            case '<':
                c = getNextChar();
                if (c == '<') {
                    getNextChar();
                    return new Token(TokenType.Dịch_trái, "<<", line, pos);
                }
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.Nhỏ_hơn_hoặc_bằng, "<=", line, pos);
                }
                return new Token(TokenType.So_sánh_nhỏ_hơn, "<", line, pos);
            case '>':
                c = getNextChar();
                if (c == '>') {
                    getNextChar();
                    return new Token(TokenType.Dịch_phải, ">>", line, pos);
                }
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.Lớn_hơn_hoặc_bằng, ">=", line, pos);
                }
                return new Token(TokenType.So_sánh_lớn_hơn, ">", line, pos);
            case '=':
                return follow('=', '=', '=', TokenType.So_sánh_bằng, TokenType.Toán_tử_gán, line, pos);
            case '!':
                return follow('=', '!', '=',TokenType.So_sánh_khác, TokenType.Phủ_định, line, pos);
            case '&':
                c = getNextChar();
                if (c == '&') {
                    getNextChar();
                    return new Token(TokenType.And_logic, "&&", line, pos);
                }
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.And_gán, "&=", line, pos);
                }
                return new Token(TokenType.And_bit, "&", line, pos);
            case '|':
                c = getNextChar();
                if (c == '|') {
                    getNextChar();
                    return new Token(TokenType.Or_logic, "||", line, pos);
                }
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.Or_gán, "|=", line, pos);
                }
                return new Token(TokenType.Or_bit, "|", line, pos);
            case '^':
                return follow('=', '^', '=', TokenType.Xor_gán, TokenType.Xor_bit, line, pos);
            case '"':
                return string_lit(this.chr, line, pos);
            case '{':
                getNextChar();
                return new Token(TokenType.Mở_ngoặc_nhọn, "{", line, pos);
            case '}':
                getNextChar();
                return new Token(TokenType.Đóng_ngoặc_nhọn, "}", line, pos);
            case '(':
                getNextChar();
                return new Token(TokenType.Mở_ngoặc_tròn, "(", line, pos);
            case ')':
                getNextChar();
                return new Token(TokenType.Đóng_ngoặc_tròn, ")", line, pos);
            case '+':
                c = getNextChar();
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.Cộng_gán, "+=", line, pos);
                }
                if (c == '+') {
                    getNextChar();
                    return new Token(TokenType.Tăng_1, "++", line, pos);
                }
                return new Token(TokenType.Toán_tử_cộng, "+", line, pos);
            case '-':
                c = getNextChar();
                if (c == '=') {
                    getNextChar();
                    return new Token(TokenType.Trừ_gán, "-=", line, pos);
                }
                if (c == '-') {
                    getNextChar();
                    return new Token(TokenType.Giảm_1, "--", line, pos);
                }
                return new Token(TokenType.Toán_tử_trừ, "-", line, pos);
            case '*':
                c= getNextChar();
                if(c == '=') {
                    getNextChar();
                    return new Token(TokenType.Nhân_gán, "*=", line, pos);
                }
                return new Token(TokenType.Toán_tử_nhân, "*", line, pos);
            case '%':
                getNextChar();
                return new Token(TokenType.Chia_lấy_dư, "%", line, pos);
            case ';':
                getNextChar();
                return new Token(TokenType.Dấu_chấm_phẩy, ";", line, pos);
            case ',':
                getNextChar();
                return new Token(TokenType.Dấu_phẩy, ",", line, pos);
            case '~':
                getNextChar();
                return new Token(TokenType.Đảo_bit, "~", line, pos);

            default:
                return identifier_or_integer(line, pos);
        }
    }

    char getNextLine() {
        int l = this.line;
        while (l == this.line) {
            getNextChar();
        }
        //this.chr = '\u0000';
        return this.chr;
        // TODO Auto-generated method stub	
    }

    char getNextChar() {
        this.pos++;
        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000';
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
        if (this.chr == '\n') {
            this.line++;
            this.pos = 0;
        }
        return this.chr;
    }

    char get2Char() {
        this.pos++;
        this.pos++;
        this.position++;
        this.position++;
        if (this.position >= this.s.length()) {
            this.chr = '\u0000';
            return this.chr;
        }
        this.chr = this.s.charAt(this.position);
        if (this.chr == '\n') {
            this.line++;
            this.pos = 0;
        }
        return this.chr;
    }

    void printTokens() {
        Token t;
        while ((t = getToken()).tokentype != TokenType.End_of_input) {
            System.out.println(t);
        }
        System.out.println(t);
    }
    /*
     public static void main(String[] args) {
     try {
 
     File f = new File("C:/Users/PepperMint/Desktop/test.txt");
     Scanner s = new Scanner(f);
     String source = "";
     System.out.print("   Line    Position    TokenType       Char\n");
     while (s.hasNext()) {
     source += s.nextLine() + "\n";
     }
     Lexer l = new Lexer(source);
     l.printTokens();
     } catch(FileNotFoundException e) {
     error(-1, -1, "Exception: " + e.getMessage());
     }
     } */
}
