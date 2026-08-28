import React from "react";

function Navbar() {
    return (
        <nav className="bg-white shadow-md rounded-lg sticky top-0 left-0 right-0 z-10 border-b border-gray-200">
            <div className="max-w-6xl mx-auto flex justify-between items-center gap-4 px-4">
                <a
                    href="/"
                    className="text-lg font-bold text-gray-700 hover:text-gray-900"
                >
                    CampusGig
                </a>

                <a
                    href="/login"
                    className="text-lg font-bold text-gray-700 hover:text-gray-900"
                >
                    Login
                </a>

                <a
                    href="/register"
                    className="text-lg font-bold text-gray-700 hover:text-gray-900"
                >
                    Register
                </a>
            </div>
        </nav>
    );
}

export default Navbar;