import React from "react";

function Footer() {
    return (
        <footer
            className="bg-white shadow-md rounded-lg border-t border-gray-200 p-6 py-4"
            style={{ backgroundColor: "#f5f5f5" }}
        >
            <div className="max-w-6xl mx-auto flex flex-col items-center gap-4 px-4">
                <p className="text-gray-500 text-sm">
                    © 2026 CampusGig. All rights reserved.
                </p>

                <div className="flex gap-4">
                    <a
                        href="/terms"
                        className="text-gray-500 hover:text-gray-900"
                    >
                        Terms
                    </a>

                    <a
                        href="/privacy"
                        className="text-gray-500 hover:text-gray-900"
                    >
                        Privacy Policy
                    </a>

                    <a
                        href="/admin"
                        className="text-gray-500 hover:text-gray-900"
                    >
                        Admin Panel
                    </a>

                    <a
                        href="https://github.com/254jeff/CampusGig"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="text-gray-500 hover:text-gray-900"
                    >
                        GitHub
                    </a>
                </div>
            </div>
        </footer>
    );
}

export default Footer;