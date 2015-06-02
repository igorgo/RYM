package home.go.rym.orm;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Artists")
public class Artist extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "NameSort")
    public String nameSort;

    @Column(name = "FirstChar", index = true)
    public char firstChar;

    @Column(name = "NameUrl", index = true)
    public String nameUrl;

    public Artist() {
        super();
    }

    public Artist (String name, String nameUrl) {
        this.name = name;
        this.nameUrl = nameUrl;
        if ((name.startsWith("The ")) && (name.length() > 4) ){
            this.nameSort = name.substring(4);
            this.firstChar = name.charAt(4);
        } else if ((name.startsWith("A ")) && (name.length() > 2)) {
            this.nameSort = name.substring(2);
            this.firstChar = name.charAt(2);
        } else {
            this.nameSort = name;
            this.firstChar = name.charAt(0);
        }

    }
}
