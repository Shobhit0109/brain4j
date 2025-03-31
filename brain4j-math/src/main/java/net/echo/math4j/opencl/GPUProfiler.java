package net.echo.math4j.opencl;

import net.echo.math4j.BrainUtils;
import org.jocl.*;

import static org.jocl.CL.*;

public class GPUProfiler {
    
    private static boolean profilingEnabled = false;
    
    public static void enableProfiling() {
        profilingEnabled = true;
    }
    
    public static void disableProfiling() {
        profilingEnabled = false;
    }
    
    public static boolean isProfilingEnabled() {
        return profilingEnabled;
    }
    
    public static cl_command_queue createCommandQueue(cl_context context, cl_device_id device) {
        long properties = 0;
        if (profilingEnabled) {
            properties = CL_QUEUE_PROFILING_ENABLE;
        }
        
        return clCreateCommandQueue(context, device, properties, null);
    }
    
    public static void profileKernel(String kernelName, cl_command_queue queue, cl_kernel kernel,
                                   int dimensions, long[] globalWorkSize, long[] localWorkSize) {
        if (!profilingEnabled) {
            clEnqueueNDRangeKernel(queue, kernel, dimensions, null, globalWorkSize, localWorkSize, 0, null, null);
            return;
        }
        
        cl_event event = new cl_event();
        
        clEnqueueNDRangeKernel(queue, kernel, dimensions, null, globalWorkSize, localWorkSize, 0, null, event);
        
        clFinish(queue);
        
        long[] startTime = new long[1];
        long[] endTime = new long[1];
        
        clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_START, 
                               Sizeof.cl_ulong, Pointer.to(startTime), null);
        clGetEventProfilingInfo(event, CL_PROFILING_COMMAND_END, 
                               Sizeof.cl_ulong, Pointer.to(endTime), null);
        
        double executionTimeMs = (endTime[0] - startTime[0]) / 1000000.0;
        System.out.printf("Kernel '%s' execution time: %.3f ms%n", kernelName, executionTimeMs);
        
        clReleaseEvent(event);
    }

    public static void printDefaultDeviceInfo() {
        cl_device_id device = DeviceUtils.getDevice();
        printDeviceInfo(device);
    }

    public static void printDeviceInfo(cl_device_id device) {
        try {
            byte[] nameBytes = new byte[1024];
            clGetDeviceInfo(device, CL_DEVICE_NAME, nameBytes.length, Pointer.to(nameBytes), null);
            String deviceName = new String(nameBytes).trim();
            
            byte[] vendorBytes = new byte[1024];
            clGetDeviceInfo(device, CL_DEVICE_VENDOR, vendorBytes.length, Pointer.to(vendorBytes), null);
            String deviceVendor = new String(vendorBytes).trim();
            
            byte[] versionBytes = new byte[1024];
            clGetDeviceInfo(device, CL_DEVICE_VERSION, versionBytes.length, Pointer.to(versionBytes), null);
            String deviceVersion = new String(versionBytes).trim();
            
            long[] globalMemSize = new long[1];
            clGetDeviceInfo(device, CL_DEVICE_GLOBAL_MEM_SIZE, Sizeof.cl_ulong, Pointer.to(globalMemSize), null);
            
            long[] localMemSize = new long[1];
            clGetDeviceInfo(device, CL_DEVICE_LOCAL_MEM_SIZE, Sizeof.cl_ulong, Pointer.to(localMemSize), null);
            
            long[] maxWorkGroupSize = new long[1];
            clGetDeviceInfo(device, CL_DEVICE_MAX_WORK_GROUP_SIZE, Sizeof.cl_long, 
                           Pointer.to(maxWorkGroupSize), null);

            String specs = BrainUtils.getHeader(" GPU Specs ") +
                    "Name: " + deviceName + "\n" +
                    "Vendor: " + deviceVendor + "\n" +
                    "OpenCL Version: " + deviceVersion + "\n" +
                    "Global Memory: " + globalMemSize[0] / (1024 * 1024) + " MB\n" +
                    "Local Memory: " + localMemSize[0] / 1024 + " KB\n" +
                    "Max Work Group Size: " + maxWorkGroupSize[0] + "\n" +
                    BrainUtils.getHeader("");

            System.out.println(specs);
        } catch (Exception e) {
            System.err.println("Error retrieving device information: " + e.getMessage());
        }
    }
} 