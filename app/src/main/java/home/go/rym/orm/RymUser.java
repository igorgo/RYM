package home.go.rym.orm;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * It's a part of project RYM
 * Created by igorgo (igor-go@parus.com.ua)
 * Copyright (C) 2015 Parus-Ukraine Corporation (www.parus.ua)
 */

@Table(name = "Users")
public class RymUser  extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Self")
    public int self;

    @Column(name = "Friend")
    public int friend;

    @Column(name = "Favorite")
    public int favorite;
}
