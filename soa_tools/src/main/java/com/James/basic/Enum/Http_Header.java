package com.James.basic.Enum;

/**
 * Created by James on 2017/2/4.
 */

public enum Http_Header {

    x_track_id("x-trackingID", "trackingID"), //
    x_sequence("x-seq", "sequence"), //
    x_referer_ip("x-ref-ip", "referer_ip");

    public String key;
    public String note;

    private Http_Header(String key, String note) {
      this.key = key;
      this.note = note;
    }
  }

