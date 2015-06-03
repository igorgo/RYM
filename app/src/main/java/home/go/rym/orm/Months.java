package home.go.rym.orm;

public enum Months {
    NUN,
    Jan,
    Feb,
    Mar,
    Apr,
    May,
    Jun,
    Jul,
    Aug,
    Sep,
    Oct,
    Nov,
    Dec;


    public String no() {
        return String.format("%02d", this.ordinal());
    }
}
