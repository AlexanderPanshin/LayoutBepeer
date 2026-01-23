package keyboard;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

import java.util.HashMap;
import java.util.Map;

    public class KeyboardLayoutDetector {

    private static final User32 USER32 = User32.INSTANCE;

    private static final Map<Integer, String> LANG_NAMES = new HashMap<>();
    static {
    LANG_NAMES.put(0x0409, "АНГЛИЙСКИЙ");
    LANG_NAMES.put(0x0809, "АНГЛИЙСКИЙ");
    LANG_NAMES.put(0x0C09, "АНГЛИЙСКИЙ");
    LANG_NAMES.put(0x0419, "РУССКИЙ");
    LANG_NAMES.put(0x0819, "РУССКИЙ");
    LANG_NAMES.put(0x0407, "НЕМЕЦКИЙ");
    LANG_NAMES.put(0x040C, "ФРАНЦУЗСКИЙ");
    LANG_NAMES.put(0x0410, "ИТАЛЬЯНСКИЙ");
    LANG_NAMES.put(0x040A, "ИСПАНСКИЙ");
    }

    private String previousLayoutCode;

    public KeyboardLayoutDetector() {
    this.previousLayoutCode = getCurrentLayoutCode();
    }

    private int getCurrentLayoutLangId() {
    try {
    WinDef.HWND hwnd = USER32.GetForegroundWindow();
    WinDef.HKL hkl;

        if (hwnd == null) {
            hkl = USER32.GetKeyboardLayout(0);
        } else {
            IntByReference pidRef = new IntByReference();
            int threadId = USER32.GetWindowThreadProcessId(hwnd, pidRef);
            if (threadId == 0) {
                hkl = USER32.GetKeyboardLayout(0);
            } else {
                hkl = USER32.GetKeyboardLayout(threadId);
            }
        }

        if (hkl == null || hkl.getPointer() == null) {
            return -1;
        }

        long ptr = Pointer.nativeValue(hkl.getPointer());
        return (int) (ptr & 0xFFFFL);
    } catch (Throwable t) {
        System.err.println("Ошибка определения раскладки: " + t.getMessage());
        return -1;
    }


    }

    public String getCurrentLayoutCode() {
    int id = getCurrentLayoutLangId();
    if (id < 0) return "ERROR";
    return String.format("0x%04X", id);
    }

    public String getCurrentLanguageName() {
    int id = getCurrentLayoutLangId();
    if (id < 0) return "НЕИЗВЕСТНО";
    String name = LANG_NAMES.get(id);
    if (name != null) return name;
    return String.format("ЯЗЫК 0x%04X", id);
    }

    public boolean hasLayoutChanged() {
    String currentCode = getCurrentLayoutCode();
    if ("ERROR".equals(currentCode)) return false;

    boolean changed = !currentCode.equals(previousLayoutCode);

    if (changed) {
        System.out.println("Раскладка изменилась: " + previousLayoutCode + " -> " + currentCode);
        previousLayoutCode = currentCode;
    }

    return changed;


    }

    public String getCurrentLayoutInfo() {
    return getCurrentLanguageName() + " (" + getCurrentLayoutCode() + ")";
    }
    }



