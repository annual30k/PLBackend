package org.dromara.patrol.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compares firmware versions by numeric components instead of concatenating digits.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class FirmwareVersionComparator {

    private static final Pattern NUMBER = Pattern.compile("\\d+");

    static boolean isParsable(String version) {
        return version != null && NUMBER.matcher(version).find();
    }

    static int compare(String left, String right) {
        ParsedVersion first = parse(left);
        ParsedVersion second = parse(right);
        int componentResult = compareComponents(first.components, second.components);
        if (componentResult != 0) {
            return componentResult;
        }
        return compareQualifier(first.qualifier, second.qualifier);
    }

    private static ParsedVersion parse(String version) {
        if (!isParsable(version)) {
            throw new IllegalArgumentException("Firmware version has no numeric component: " + version);
        }
        String normalized = version.trim();
        int buildIndex = normalized.indexOf('+');
        if (buildIndex >= 0) {
            normalized = normalized.substring(0, buildIndex);
        }
        Matcher firstNumber = NUMBER.matcher(normalized);
        firstNumber.find();
        int qualifierIndex = normalized.indexOf('-', firstNumber.start() + 1);
        String core = qualifierIndex >= 0 ? normalized.substring(0, qualifierIndex) : normalized;
        String qualifier = qualifierIndex >= 0 ? normalized.substring(qualifierIndex + 1) : "";
        Matcher matcher = NUMBER.matcher(core);
        List<BigInteger> components = new ArrayList<>();
        while (matcher.find()) {
            components.add(new BigInteger(matcher.group()));
        }
        return new ParsedVersion(components, qualifier.toLowerCase(Locale.ROOT));
    }

    private static int compareComponents(List<BigInteger> left, List<BigInteger> right) {
        int count = Math.max(left.size(), right.size());
        for (int i = 0; i < count; i++) {
            BigInteger first = i < left.size() ? left.get(i) : BigInteger.ZERO;
            BigInteger second = i < right.size() ? right.get(i) : BigInteger.ZERO;
            int result = first.compareTo(second);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private static int compareQualifier(String left, String right) {
        if (left.isBlank() && right.isBlank()) {
            return 0;
        }
        if (left.isBlank()) {
            return 1;
        }
        if (right.isBlank()) {
            return -1;
        }
        String[] firstParts = left.split("[._-]");
        String[] secondParts = right.split("[._-]");
        int count = Math.max(firstParts.length, secondParts.length);
        for (int i = 0; i < count; i++) {
            if (i >= firstParts.length) return -1;
            if (i >= secondParts.length) return 1;
            String first = firstParts[i];
            String second = secondParts[i];
            boolean firstNumeric = first.matches("\\d+");
            boolean secondNumeric = second.matches("\\d+");
            int result;
            if (firstNumeric && secondNumeric) {
                result = new BigInteger(first).compareTo(new BigInteger(second));
            } else if (firstNumeric != secondNumeric) {
                result = firstNumeric ? -1 : 1;
            } else {
                result = first.compareTo(second);
            }
            if (result != 0) return result;
        }
        return 0;
    }

    private static final class ParsedVersion {
        private final List<BigInteger> components;
        private final String qualifier;

        private ParsedVersion(List<BigInteger> components, String qualifier) {
            this.components = components;
            this.qualifier = qualifier;
        }
    }
}
