//package hsb.lol.lolfight.windowapi;
//
//
//
//import jdk.jfr.MemoryAddress;
//
//import java.lang.foreign.FunctionDescriptor;
//import java.lang.foreign.MemorySegment;
//import java.lang.foreign.SymbolLookup;
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodType;
//import java.nio.ByteOrder;
//import java.nio.IntBuffer;
//import java.util.Arrays;
//
///**
// * @author hsb
// * @date 2023/8/27 16:39
// */
//public class Test {
//    public static void main(String[] args) throws Throwable {
//
//        System.loadLibrary("User32");
//        System.loadLibrary("kernel32");
//
//        SymbolLookup loaderSyms = SymbolLookup.loaderLookup();
//
//        MemoryAddress addr = loaderSyms.lookup("FindWindowA").get();
//
//
//
//        MethodHandle FindWindow = CLinker.getInstance().downcallHandle(
//                loaderSyms.lookup("FindWindowA").get(),
//                MethodType.methodType(MemoryAddress.class, MemoryAddress.class, MemoryAddress.class),
//                FunctionDescriptor.of(CLinker.C_POINTER, CLinker.C_POINTER, CLinker.C_POINTER));
//        MethodHandle GetWindowRect = CLinker.getInstance().downcallHandle(
//                loaderSyms.lookup("GetWindowRect").get(),
//                MethodType.methodType(int.class, MemoryAddress.class, MemoryAddress.class),
//                FunctionDescriptor.of(CLinker.C_INT, CLinker.C_POINTER, CLinker.C_POINTER));
//
//        String className = "Chrome_RenderWidgetHostHWND";
//        String windowName = "Chrome Legacy Window";
//
//        ResourceScope scope = ResourceScope.globalScope();
//        MemorySegment w2 = CLinker.toCString(windowName, scope);
//
//
//
//        MemoryAddress address = (MemoryAddress) FindWindow.invoke(CLinker.toCString(className, scope).address(),w2.address());
//        //MemoryAddress address = (MemoryAddress) FindWindow.invoke(MemoryAddress.NULL,w2.address());
//
//        MemorySegment segment2 = MemorySegment.allocateNative(16, scope);
//        System.out.println(segment2);
//        int result = (int)GetWindowRect.invoke(address, segment2.address());
//        System.out.println(result);
//        System.out.println(segment2);
//        byte[] byteArray1 = segment2.toByteArray();
//
//
//        int intAtIndex = MemoryAccess.getIntAtIndex(segment2, 0, ByteOrder.BIG_ENDIAN);
//        System.out.println(intAtIndex);
//
//
//        System.out.println(Arrays.toString(byteArray1));
//    }
//
//    public static void  charToLPCWSTR(String str) throws Throwable {
//        ResourceScope scope = ResourceScope.globalScope();
//        MemorySegment w2 = CLinker.toCString(str, scope);
//        SymbolLookup loaderSyms = SymbolLookup.loaderLookup();
//        MethodHandle MultiByteToWideChar = CLinker.getInstance().downcallHandle(
//                loaderSyms.lookup("MultiByteToWideChar").get(),
//                MethodType.methodType(int.class, int.class, int.class, MemoryAddress.class, int.class, MemoryAddress.class, int.class),
//                FunctionDescriptor.of(CLinker.C_INT, CLinker.C_INT, CLinker.C_INT, CLinker.C_POINTER, CLinker.C_INT, CLinker.C_POINTER, CLinker.C_INT));
//
//
//        int len = (int) MultiByteToWideChar.invoke(65001, 0, w2.address(), -1, MemoryAddress.NULL, 0);
//        System.out.println(len);
//        MemorySegment memorySegment = MemorySegment.allocateNative(len * 2L, scope);
//        MultiByteToWideChar.invoke(65001, 0, w2.address(), -1, memorySegment.address(), len);
//
//        //memorySegment中就存放了结果
//
//    }
//
//}
