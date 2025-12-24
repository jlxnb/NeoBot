package dev.neovoxel.neobot.util.http;

import org.jetbrains.annotations.NotNull;

public class HttpPost extends HttpClient {
    protected HttpPost(String url) {
        super(url);
        this.method = HttpRequestMethod.POST;
    }
}
