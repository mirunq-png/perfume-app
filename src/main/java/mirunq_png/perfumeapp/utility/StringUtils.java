package mirunq_png.perfumeapp.utility;

public class StringUtils
{
    private StringUtils() {}
    public static String formatText(String input)
    {
        StringBuilder sb=new StringBuilder();
        String[] cuv=input.toLowerCase().split("\\s+"); // in case of multiple spaces
        for (int i=0;i<cuv.length;i++)
        {
            if (cuv[i].charAt(0)>='a'&&cuv[i].charAt(0)<='z')
            {
                sb.append((char) (cuv[i].charAt(0) - 32)); //a->A
                sb.append(cuv[i].substring(1));
            }
            else sb.append(cuv[i]);
            sb.append(" ");
        }
        return sb.toString();
    }
}
