package home.go.rym.orm;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


@Table(name = "Releases")
public class Release extends Model {

    @Column(name = "Artist", index = true)
    public Artist artist;

    @Column(name = "Type")
    public ReleaseType type;

    @Column(name = "Name")
    public String name;

    @Column(name = "Year")
    public int year;

    @Column(name = "NameUrl", index = true)
    public String nameUrl;


}
