package it.hackerinside.etk.Utils;

public final class FileSizeFormatter {

    private static final long KIB = 1024L;
    private static final long MIB = KIB * 1024L;
    private static final long GIB = MIB * 1024L;
    private static final long TIB = GIB * 1024L;

    private FileSizeFormatter() {
    }

    public static String format(long bytes) {
        if (bytes >= TIB) {
            return String.format("%.2f TiB", (double) bytes / TIB);
        }
        if (bytes >= GIB) {
            return String.format("%.2f GiB", (double) bytes / GIB);
        }
        if (bytes >= MIB) {
            return String.format("%.2f MiB", (double) bytes / MIB);
        }
        if (bytes >= KIB) {
            return String.format("%.2f KiB", (double) bytes / KIB);
        }
        return bytes + " bytes";
    }
}
