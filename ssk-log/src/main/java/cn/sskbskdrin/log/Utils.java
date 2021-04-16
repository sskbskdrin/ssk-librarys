package cn.sskbskdrin.log;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by ex-keayuan001 on 2019-08-09.
 *
 * @author ex-keayuan001
 */
class Utils {

    static final String NULL = "null";

    static void objToString(StringBuilder builder, Object obj) {
        if (builder == null) {
            return;
        }
        if (obj == null) {
            builder.append(NULL);
        } else if (obj instanceof Throwable) {
            builder.append('\n');
            builder.append(getStackTraceString((Throwable) obj));
        } else if (obj.getClass().isArray()) {
            builder.append('[');
            arrayString(builder, obj);
            builder.append(']');
        } else if (obj instanceof String) {
            String temp = (String) obj;
            if (LogManager.enableXML) {
                temp = xml((String) obj, 2);
            }
            if (LogManager.enableJson) {
                temp = JSONUtils.json(temp, 2);
            }
            builder.append(temp);
        } else if (LogManager.enableJson) {
            if (!JSONUtils.objToString(builder, obj)) {
                builder.append(obj);
            }
        } else {
            builder.append(obj);
        }
    }

    private static void arrayString(StringBuilder builder, Object obj) {
        if (obj instanceof boolean[]) {
            boolString(builder, (boolean[]) obj);
        } else if (obj instanceof byte[]) {
            byteString(builder, (byte[]) obj);
        } else if (obj instanceof char[]) {
            charString(builder, (char[]) obj);
        } else if (obj instanceof short[]) {
            shortString(builder, (short[]) obj);
        } else if (obj instanceof int[]) {
            intString(builder, (int[]) obj);
        } else if (obj instanceof float[]) {
            floatString(builder, (float[]) obj);
        } else if (obj instanceof long[]) {
            longString(builder, (long[]) obj);
        } else if (obj instanceof double[]) {
            doubleString(builder, (double[]) obj);
        } else {
            objectString(builder, (Object[]) obj);
        }
    }

    private static void boolString(StringBuilder builder, boolean[] value) {
        if (value.length > 0) {
            for (boolean b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void byteString(StringBuilder builder, byte[] value) {
        if (value.length > 0) {
            for (byte b : value) {
                builder.append(Integer.toHexString(b & 0xff));
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void charString(StringBuilder builder, char[] value) {
        if (value.length > 0) {
            for (char b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void shortString(StringBuilder builder, short[] value) {
        if (value.length > 0) {
            for (short b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void intString(StringBuilder builder, int[] value) {
        if (value.length > 0) {
            for (int b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void floatString(StringBuilder builder, float[] value) {
        if (value.length > 0) {
            for (float b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void longString(StringBuilder builder, long[] value) {
        if (value.length > 0) {
            for (long b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void doubleString(StringBuilder builder, double[] value) {
        if (value.length > 0) {
            for (double b : value) {
                builder.append(b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static void objectString(StringBuilder builder, Object[] value) {
        if (value.length > 0) {
            for (Object b : value) {
                objToString(builder, b);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
    }

    private static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return NULL;
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    private static String xml(String xml, int indent) {
        try {
            if (xml.startsWith("<")) {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "html");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
                transformer.transform(xmlInput, xmlOutput);
                return "\n" + xmlOutput.getWriter().toString() + "\n";
            }
        } catch (TransformerException ignored) {
        }
        return xml;
    }
}
