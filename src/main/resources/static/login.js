
"use strict";

document.addEventListener("DOMContentLoaded", async () => {

    const form = document.getElementById("loginForm");
    const csrfField = document.getElementById("csrfField");
    const message = document.getElementById("message");
    const submitButton = document.getElementById("submit");

    const params = new URLSearchParams(window.location.search);

    let csrfReady = false;
    let submitting = false;

    submitButton.disabled = true;

    function showMessage(text) {
        message.textContent = text;
    }

    async function loadCsrf() {

        csrfReady = false;
        csrfField.value = "";
        submitButton.disabled = true;

        try {

            const response = await fetch("/api/csrf", {
                method: "GET",
                credentials: "same-origin",
                cache: "no-store"
            });

            if (!response.ok) {
                throw new Error(
                    "HTTP " + response.status
                );
            }

            const result = await response.json();

            if (!result.token) {
                throw new Error("CSRF token không hợp lệ");
            }

            csrfField.value = result.token;

            csrfReady = true;
            submitButton.disabled = false;

            return true;

        } catch (error) {

            console.error("CSRF Error:", error);

            showMessage(
                "Không thể tạo phiên đăng nhập bảo mật. " +
                "Hãy tải lại trang."
            );

            return false;
        }
    }

    if (params.has("error")) {
        showMessage(
            "Đăng nhập chưa thành công. " +
            "Vui lòng kiểm tra tài khoản và mật khẩu."
        );
    }

    if (params.has("logout")) {
        showMessage("Bạn đã đăng xuất thành công.");
    }

    // Lấy token trước khi cho phép đăng nhập.
    await loadCsrf();

    form.addEventListener("submit", async event => {

        event.preventDefault();

        if (submitting) return;

        if (!csrfReady) {
            showMessage(
                "Phiên bảo mật chưa sẵn sàng. Đang thử lại..."
            );

            await loadCsrf();
            return;
        }

        submitting = true;
        submitButton.disabled = true;

        const oldHTML = submitButton.innerHTML;

        submitButton.textContent = "Đang đăng nhập...";
        showMessage("");

        try {

            const formData = new FormData(form);

            // Gửi form POST với CSRF và cookie cùng phiên.
            const response = await fetch("/login", {
                method: "POST",
                body: new URLSearchParams(formData),
                credentials: "same-origin",
                redirect: "manual",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                }
            });

            // Spring Security chuyển hướng sau xác thực.
            if (
                response.type === "opaqueredirect" ||
                (response.status >= 300 &&
                 response.status < 400)
            ) {

                // Xác nhận rằng phiên thật sự đăng nhập.
                const check = await fetch("/api/me", {
                    credentials: "same-origin",
                    cache: "no-store"
                });

                if (check.ok) {
                    window.location.replace("/");
                    return;
                }

                throw new Error(
                    "Tài khoản hoặc mật khẩu không đúng, " +
                    "hoặc phiên đăng nhập chưa được xác nhận."
                );
            }

            if (response.status === 403) {
                throw new Error(
                    "Phiên CSRF không hợp lệ. " +
                    "Hãy thử đăng nhập lại."
                );
            }

            if (response.status === 401) {
                throw new Error(
                    "Tài khoản hoặc mật khẩu không đúng."
                );
            }

            if (!response.ok) {
                throw new Error(
                    "Máy chủ trả về HTTP " + response.status
                );
            }

            const check = await fetch("/api/me", {
                credentials: "same-origin",
                cache: "no-store"
            });

            if (check.ok) {
                window.location.replace("/");
                return;
            }

            throw new Error(
                "Không xác nhận được phiên đăng nhập."
            );

        } catch (error) {

            console.error("Login Error:", error);

            showMessage(error.message);

            submitting = false;

            submitButton.innerHTML = oldHTML;

            // Mỗi lần thử lại sẽ xin token mới.
            await loadCsrf();
        }
    });
});
