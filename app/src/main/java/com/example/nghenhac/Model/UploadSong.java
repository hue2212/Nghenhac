package com.example.nghenhac.Model;

public class UploadSong {

    public  String songsCategory,songTile,arlist,album_art,songDuration, songLink,mKey;

    public UploadSong(String songsCategory, String songTile, String arlist, String album_art, String songDuration, String songLink, String mKey) {
        if(songTile.trim().equals("")){
            songTile = " No title";
        }

        this.songsCategory = songsCategory;
        this.songTile = songTile;
        this.arlist = arlist;
        this.album_art = album_art;
        this.songDuration = songDuration;
        this.songLink = songLink;
    }

    public UploadSong(String songCategory, String title1, String artist1, String album_art1, String durations1, String s) {
    }

    public String getArlist() {
        return arlist;
    }

    public void setArlist(String arlist) {
        this.arlist = arlist;
    }
    public String getSongTile() {
        return songTile;
    }

    public void setSongTile(String songTile) {
        this.songTile = songTile;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getSongsCategory() {
        return songsCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songsCategory = songsCategory;
    }
}
