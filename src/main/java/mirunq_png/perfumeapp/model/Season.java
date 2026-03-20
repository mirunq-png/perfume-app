package mirunq_png.perfumeapp.model;

public enum Season
{
    WINTER, SPRING, SUMMER, FALL;

    public static Season fromId(int id)
    {
        return switch (id) {
            case 1 -> WINTER;
            case 2 -> SPRING;
            case 3 -> SUMMER;
            case 4 -> FALL;
            default -> throw new IllegalArgumentException("Invalid ID: " + id);
        };
    }
}
