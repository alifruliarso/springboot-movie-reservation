package com.galapea.techblog.moviereservation.service;

import com.github.f4b6a3.tsid.TsidCreator;

public class KeyGenerator {
    public static String next(String prefix) {
		return prefix + TsidCreator.getTsid().format("%S");
	}
}
