@echo off
reg query HKEY_CURRENT_USER\SOFTWARE\Microsoft\Windows\CurrentVersion\Run /v Check>nul 2>nul&&echo true||echo false