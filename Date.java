class Date implements Comparable<Date> {
  int year;
  int month;
  int day;

  public Date(int aar, int mnd, int dag) {
    year = aar;
    month = mnd;
    day = dag;
  }

  @Override
  public int compareTo(Date other) {
    if (year < other.year) return -1;
    if (year > other.year) return 1;
    if (month < other.month) return -1;
    if (month > other.month) return 1;
    if (day < other.day) return -1;
    if (day > other.day) return 1;
    return 0;
  }

  @Override
  public String toString() {
    String strDay = ""+day;
    String strMonth = ""+month;
    if (day < 10) {
      strDay = "0"+strDay;
    }
    if (month < 10) {
      strMonth = "0"+strMonth;
    }
    return strDay+"/"+strMonth+"-"+year;
  }
}
