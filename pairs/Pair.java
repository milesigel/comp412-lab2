package pairs;

import intermediate_representation.IRList;

public class Pair {

    public int token;
    public String lexeme;
    public int lineNum;

    public Pair(int token, String lexeme, int lineNum) {
        this.token = token;
        this.lexeme = lexeme;
        this.lineNum = lineNum;
    }


    @Override
    public String toString() {
        if (this.token == -1) {
            return "ERROR " + lineNum + ": " + this.lexeme;
        }
        return lineNum + ": <" + IRList.tokenConversion[this.token] + ", " + this.lexeme + ">";
    }
}
