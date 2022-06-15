package org.test;

import org.mapper.DateFormat;
import org.mapper.PropertyName;

import java.time.LocalDateTime;
import java.util.ArrayList;

public record CDAlbum(String name, String artist,
                      @DateFormat("yyyy/MM/dd HH:mm:ss") LocalDateTime release,
                      @PropertyName("песни") ArrayList<String> tracks)
{}
