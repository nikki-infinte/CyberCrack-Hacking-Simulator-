package com.example.cybercrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*




import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cybercrack.ui.theme.CyberCrackTheme

class Terminal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberCrackTheme {
                TerminalScreen()
            }
        }
    }
}
// Updated TerminalScreen with hacking tools
@Composable
fun TerminalScreen() {
    var command by remember { mutableStateOf("") }
    var commandHistory by remember { mutableStateOf(
        listOf(
            "Welcome to CyberCrack Ethical Hacking Terminal",
            "Type 'help' for available commands",
            "Type 'tools' for available hacking tools",
            "NOTE: This is a simulation only - no real systems are affected"
        )
    )}
    val scrollState = rememberScrollState()
    var currentTarget by remember { mutableStateOf<Target?>(null) }

    // Scroll to bottom whenever command history changes
    LaunchedEffect(commandHistory) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                commandHistory.forEach { line ->
                    Text(
                        text = line,
                        color = if (line.startsWith("ERROR:")) Color.Red else Color.Green,
                        fontSize = 16.sp,
                        style = TextStyle(fontFamily = FontFamily.Monospace)
                    )
                }
            }

            Row {
                Text(
                    text = "> ",
                    color = Color.Green,
                    fontSize = 16.sp
                )

                TerminalInputField(
                    value = command,
                    onValueChange = { command = it },
                    onExecute = {
                        if (command.isNotBlank()) {
                            val result = processCommand(command, currentTarget) { newTarget ->
                                currentTarget = newTarget
                            }
                            commandHistory = commandHistory + "> $command" + result.output
                            if (result.clearScreen) {
                                commandHistory = emptyList()
                            }
                            command = ""
                        }
                    }
                )

                BlinkingCursor()
            }

            Spacer(modifier = Modifier.height(8.dp))

            currentTarget?.let { target ->
                TargetInfoCard(target)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (command.isNotBlank()) {
                        val result = processCommand(command, currentTarget) { newTarget ->
                            currentTarget = newTarget
                        }
                        commandHistory = commandHistory + "> $command" + result.output
                        if (result.clearScreen) {
                            commandHistory = emptyList()
                        }
                        command = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Execute", color = Color.White)
            }
        }
    }
}

@Composable
fun TerminalInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onExecute: () -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onExecute() }),
        textStyle = TextStyle(color = Color.Green, fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
    )
}


@Composable
fun TargetInfoCard(target: Target) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Current Target: ${target.name}",
                color = Color.Cyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Difficulty: ${target.difficulty}",
                color = Color.White
            )
            Text(
                text = "Description: ${target.description}",
                color = Color.White
            )
        }
    }
}

// Enhanced command processing
fun processCommand(
    command: String,
    currentTarget: Target?,
    onTargetChange: (Target?) -> Unit
): CommandResult {
    val parts = command.split(" ")
    val cmd = parts[0].lowercase()
    val args = parts.drop(1)

    return when (cmd) {
        "help" -> CommandResult(
            """
            Available commands:
            - help: Show this help
            - clear: Clear terminal
            - tools: List available hacking tools
            - targets: List available virtual targets
            - set-target <id>: Set current target
            - scan: Scan current target
            - exploit: Attempt to exploit vulnerabilities
            - about: About this terminal
            """.trimIndent()
        )

        "clear" -> CommandResult("", clearScreen = true)

        "tools" -> CommandResult(
            """
            Available tools:
            - nmap: Network scanner
            - metasploit: Exploitation framework
            - sqlmap: SQL injection tool
            - hydra: Password cracking tool
            - john: Password cracking tool
            - aircrack-ng: Wireless security tool
            - burpsuite: Web vulnerability scanner
            
            Usage: toolname [options]
            Example: nmap -sV target.virtual
            """.trimIndent()
        )

        "targets" -> CommandResult(
            """
            Available Virtual Targets:
            1. website.virtual (Easy) - Simple vulnerable website
            2. server.virtual (Medium) - Linux server with misconfigurations
            3. network.virtual (Hard) - Corporate network simulation
            4. bank.virtual (Expert) - Financial system simulation
            
            Use 'set-target <id>' to select target
            """.trimIndent()
        )

        "set-target" -> {
            if (args.isEmpty()) {
                CommandResult("ERROR: Please specify target ID")
            } else {
                val target = when (args[0]) {
                    "1" -> Target(
                        "website.virtual",
                        "Easy",
                        "Vulnerable web application with common flaws (SQLi, XSS)"
                    )
                    "2" -> Target(
                        "server.virtual",
                        "Medium",
                        "Misconfigured Linux server with outdated services"
                    )
                    "3" -> Target(
                        "network.virtual",
                        "Hard",
                        "Corporate network with multiple systems and defenses"
                    )
                    "4" -> Target(
                        "bank.virtual",
                        "Expert",
                        "Financial system with advanced security measures"
                    )
                    else -> null
                }

                target?.let {
                    onTargetChange(it)
                    CommandResult("Target set to ${it.name}")
                } ?: CommandResult("ERROR: Invalid target ID")
            }
        }

        "scan" -> {
            currentTarget?.let { target ->
                CommandResult(
                    """
                    Scanning ${target.name}...
                    
                    PORT     STATE    SERVICE
                    22/tcp   open     SSH
                    80/tcp   open     HTTP
                    443/tcp  open     HTTPS
                    3306/tcp open     MySQL
                    
                    Vulnerabilities detected:
                    - Outdated Apache version (2.4.29)
                    - MySQL with weak credentials
                    - Potential XSS on login page
                    """.trimIndent()
                )
            } ?: CommandResult("ERROR: No target selected. Use 'set-target' first")
        }

        "exploit" -> {
            currentTarget?.let { target ->
                when (target.name) {
                    "website.virtual" -> CommandResult(
                        """
                        Exploiting ${target.name}...
                        
                        [*] Testing for SQL injection... vulnerable!
                        [*] Testing for XSS... vulnerable!
                        [*] Testing file upload... vulnerable!
                        
                        [+] Gained admin access!
                        Database dumped to /root/website_db.sql
                        
                        Next steps:
                        - Examine database for sensitive information
                        - Upload webshell for persistent access
                        - Type 'show-data' to view extracted info
                        """.trimIndent()
                    )
                    "server.virtual" -> CommandResult(
                        """
                        Exploiting ${target.name}...
                        
                        [*] Brute-forcing SSH... success! (root:admin123)
                        [*] Exploiting Apache vulnerability... success!
                        [*] Privilege escalation via dirty cow... success!
                        
                        [+] Root access achieved!
                        System information saved to /root/server_info.txt
                        
                        Next steps:
                        - Establish persistence
                        - Dump password hashes
                        - Pivot to other systems
                        """.trimIndent()
                    )
                    else -> CommandResult(
                        """
                        Exploiting ${target.name}...
                        
                        This target requires more advanced techniques.
                        Try using specific tools:
                        - nmap -sV -O ${target.name}
                        - sqlmap -u http://${target.name}/login.php --dbs
                        - hydra -l admin -P wordlist.txt ssh://${target.name}
                        """.trimIndent()
                    )
                }
            } ?: CommandResult("ERROR: No target selected. Use 'set-target' first")
        }

        "nmap" -> CommandResult(
            """
            Nmap simulation for ${currentTarget?.name ?: "target.virtual"}
            
            Starting Nmap 7.80 ( https://nmap.org )
            Scan report for ${currentTarget?.name ?: "target.virtual"} (192.168.1.100)
            Host is up (0.0023s latency).
            
            PORT     STATE SERVICE       VERSION
            22/tcp   open ssh           OpenSSH 7.2p2 Ubuntu
            80/tcp   open http          Apache httpd 2.4.29
            443/tcp  open ssl/http      Apache httpd 2.4.29
            3306/tcp open mysql         MySQL 5.7.21
            8080/tcp open http-proxy    Squid http proxy 3.5.12
            
            Service detection performed. Please report any incorrect results.
            Nmap done: 1 IP address (1 host up) scanned in 6.53 seconds
            """.trimIndent()
        )

        "sqlmap" -> {
            val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(java.util.Date())
            CommandResult(
                """
        sqlmap simulation
        
        ___ ___[)]_____ ___ ___  {1.4.2#stable}
        |_ -| . [(]     | .'| . |
        |___|_  ["]_|_|_|__,|  _|
              |_|V          |_|   http://sqlmap.org
        
        [*] starting at: $currentTime
        
        [INFO] testing connection to the target URL
        [INFO] checking if the target is protected
        [INFO] testing if the target URL is stable
        [INFO] target URL is stable
        [INFO] testing if GET parameter is dynamic
        [INFO] confirming that GET parameter is dynamic
        [INFO] GET parameter appears to be injectable
        [INFO] the back-end DBMS is MySQL
        [WARNING] reflective value(s) found and filtering out
        
        [CRITICAL] SQL injection vulnerability detected
        
        Available databases [5]:
        - information_schema
        - admin_db
        - user_data
        - config
        - backup_2020
        
        Type 'sqlmap --dbs' to enumerate databases
        """.trimIndent()
            )
        }

        "metasploit" -> CommandResult(
            """
    [Metasploit Framework Console]
    ============================
    
    ████████╗ ██████╗ ███████╗
    ╚══██╔══╝██╔═══██╗██╔════╝
       ██║   ██║   ██║███████╗
       ██║   ██║   ██║╚════██║
       ██║   ╚██████╔╝███████║
       ╚═╝    ╚═════╝ ╚══════╝ v6.0.0-dev
    
    Core Stats:
    • 2048 exploits
    • 1105 auxiliary
    • 344 post modules
    • 562 payloads
    
    msf6 > 
    Type 'help' for basic commands
    """.trimIndent()
        )

        "about" -> CommandResult(
            """
            CyberCrack Ethical Hacking Terminal v2.0
            
            This is a simulated environment for learning ethical hacking.
            No real systems are affected by these commands.
            
            Features:
            - Virtual targets with realistic vulnerabilities
            - Common hacking tools simulation
            - Educational scenarios
            - Safe learning environment
            
            Developed for educational purposes only.
            """.trimIndent()
        )

        else -> {
            if (command.startsWith("echo ")) {
                CommandResult(command.removePrefix("echo "))
            } else {
                CommandResult("ERROR: Unknown command '$cmd'. Type 'help' for available commands")
            }
        }
    }


}

data class CommandResult(
    val output: String,
    val clearScreen: Boolean = false
)

data class Target(
    val name: String,
    val difficulty: String,
    val description: String
)


@Composable
fun BlinkingCursor() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = "_",
        color = Color.Green.copy(alpha = alpha),
        fontSize = 16.sp
    )
}