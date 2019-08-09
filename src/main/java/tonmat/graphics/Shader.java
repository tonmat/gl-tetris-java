package tonmat.graphics;

import org.lwjgl.opengl.GL20;
import tonmat.mat4f.Mat4f;
import tonmat.util.ResourceManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private static int binded;
    private final int program;
    private final Map<String, Integer> uniformsLocations = new HashMap<>();

    public Shader(String... resources) {
        final List<Integer> shaders = Stream.of(resources).map(this::createShader).collect(Collectors.toList());
        program = glCreateProgram();
        shaders.forEach(shader -> glAttachShader(program, shader));
        glLinkProgram(program);
        final int status = glGetProgrami(program, GL_LINK_STATUS);
        shaders.forEach(GL20::glDeleteShader);
        if (status != GL_TRUE) {
            final String infoLog = glGetProgramInfoLog(program);
            System.err.println("Error linking program: " + infoLog);
            glDeleteProgram(program);
        }
    }

    public static void unbind() {
        if (binded == 0)
            return;
        binded = 0;
        glUseProgram(0);
    }

    private int createShader(String resource) {
        final String ext = resource.substring(resource.lastIndexOf(".") + 1);
        final String source = ResourceManager.getString(resource);
        int type = 0;
        switch (ext) {
            case "vert":
                type = GL_VERTEX_SHADER;
                break;
            case "frag":
                type = GL_FRAGMENT_SHADER;
                break;
            default:
                System.err.println("Invalid shader type: " + ext);
                break;
        }
        final int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        final int status = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            final String infoLog = glGetShaderInfoLog(shader);
            System.err.println("Error compiling shader: " + infoLog);
            glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    public void bind() {
        if (binded == program)
            return;
        binded = program;
        glUseProgram(program);
    }

    private int getUniformLocation(String name) {
        if (uniformsLocations.containsKey(name))
            return uniformsLocations.get(name);
        final int location = glGetUniformLocation(program, name);
        uniformsLocations.put(name, location);
        return location;
    }

    public void setUniform1i(String name, int x) {
        glUniform1i(getUniformLocation(name), x);
    }

    public void setUniformMat4f(String name, Mat4f value) {
        glUniformMatrix4fv(getUniformLocation(name), false, value.array);
    }
}
