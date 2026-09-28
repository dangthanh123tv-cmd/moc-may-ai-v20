package com.mocmay.ai;

import java.util.Locale;

/** Small non-evaluating-expression calculator: no reflection, scripts, or arbitrary code. */
public final class SafeCalculator {
    private String s; private int p;
    public double eval(String expression) {
        if (expression == null || expression.length() > 512) throw new IllegalArgumentException("Biểu thức quá dài.");
        s = expression.replace("×", "*").replace("÷", "/").replace(',', '.').trim(); p = 0;
        double v = expr(); skip(); if (p != s.length()) throw new IllegalArgumentException("Biểu thức không hợp lệ tại vị trí " + p + ".");
        if (Double.isNaN(v) || Double.isInfinite(v)) throw new IllegalArgumentException("Kết quả không hữu hạn.");
        return v;
    }
    private double expr(){ double v=term(); while(true){skip(); if(match('+')) v+=term(); else if(match('-')) v-=term(); else return v;} }
    private double term(){ double v=factor(); while(true){skip(); if(match('*')) v*=factor(); else if(match('/')){double d=factor(); if(Math.abs(d)<1e-15) throw new IllegalArgumentException("Không thể chia cho 0."); v/=d;} else return v;} }
    private double factor(){ skip(); if(match('+')) return factor(); if(match('-')) return -factor(); if(match('(')){double v=expr(); if(!match(')')) throw new IllegalArgumentException("Thiếu dấu )."); return v;} int start=p; boolean dot=false; while(p<s.length()){char c=s.charAt(p); if(Character.isDigit(c)) p++; else if(c=='.'&&!dot){dot=true;p++;} else break;} if(start==p) throw new IllegalArgumentException("Thiếu số tại vị trí "+p+"."); return Double.parseDouble(s.substring(start,p)); }
    private boolean match(char c){skip(); if(p<s.length()&&s.charAt(p)==c){p++;return true;} return false;}
    private void skip(){while(p<s.length()&&Character.isWhitespace(s.charAt(p)))p++;}
    public static String format(double v){ if(Math.rint(v)==v) return String.format(Locale.ROOT,"%.0f",v); return String.format(Locale.ROOT,"%.10f",v).replaceAll("0+$","").replaceAll("\\.$",""); }
}
