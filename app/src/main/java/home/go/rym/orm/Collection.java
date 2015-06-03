package home.go.rym.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

@Table(name = "Collection")
public class Collection   extends Model {

    @Column(name = "User", index = true)
    public RymUser user;

    @Column(name = "Date")
    public String entryDate;

    @Column(name = "Release", index = true)
    public Release release;

    @Column(name = "Rate")
    public int rate;

}

