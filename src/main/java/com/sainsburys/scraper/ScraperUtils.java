package com.sainsburys.scraper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.function.Function;

/**
 * An immutable class that provides some utilities, perhaps its not OOP but its
 * safe
 * 
 * @author claude
 *
 */
public class ScraperUtils {

	final private static String KjperKcal = "0.239006";

	final public static Function<String, String> kjToKcal = kj -> {

		BigDecimal energyKcal = new BigDecimal(kj);

		energyKcal = energyKcal.multiply(new BigDecimal(KjperKcal));

		energyKcal = energyKcal.setScale(0, RoundingMode.HALF_EVEN);

		return energyKcal.toPlainString();
	};

	public static String getAbsolutePath(String currentPath, String relativePath) {

		String[] rPath = relativePath.split("(?<!/)/(?!/)");
		String[] fPath = currentPath.split("(?<!/)/(?!/)");
		int hops = (int) Arrays.asList(rPath).stream().filter(path -> {
			return path.equals("..");
		}).count();

		if (hops < 1) {
			return relativePath;

		}

		int si = fPath.length - hops;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < si - 1; i++) {
			sb.append(fPath[i]);
			sb.append("/");

		}

		for (int i = hops; i < rPath.length; i++) {

			sb.append(rPath[i]);
			if (i != rPath.length - 1) {
				sb.append("/");
			}

		}
		return sb.toString();

	}

}
