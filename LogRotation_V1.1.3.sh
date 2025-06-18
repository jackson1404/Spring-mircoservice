#!/bin/bash

# Define directories (using forward slashes for cross-platform compatibility)
LOG_DIR="C:/Users/25-00313/Documents/Win_Min_Thant/WEB_Banca_Portal_v1.1.9/logs"
TMP_DIR_TODAY="C:/Users/25-00313/Documents/Win_Min_Thant/WEB_Banca_Portal_v1.1.9/tmp_logs_today" 
TMP_DIR_OLD="C:/Users/25-00313/Documents/Win_Min_Thant/WEB_Banca_Portal_v1.1.9/tmp_logs_old"
BACKUP_DIR="C:/Users/25-00313/Documents/Win_Min_Thant/WEB_Banca_Portal_v1.1.9/Backup"

# Remote server details
REMOTE_USER="admin"
REMOTE_HOST="10.1.8.67"
REMOTE_BACKUP_DIR="C:/Users/Admin/Desktop/LOG_BACKUP"

# Get current date details
CURRENT_DATE=$(date +'%Y%m%d')
CURRENT_YYYYMM=$(date +'%Y-%m')
CURRENT_MONTH=$(date +'%m')
CURRENT_YEAR=$(date +'%Y')
START_TIME=$(date +%s)

# Log file for tracking
LOG_FILE="$LOG_DIR/${CURRENT_DATE}_LOG_ROTATION.log"

echo_log() {
    local level=$1
    local message=$2
    local timestamp=$(date +'%Y-%m-%d %H:%M:%S')
    local duration=""
    
    # Define fixed width for log level (8 chars for longest level)
    local level_padding="%-9s"
    
    if [ "$level" == "PERF" ]; then
        local current_time=$(date +%s)
        duration=" [Duration: $((current_time - START_TIME))s]"
    fi
    
    # Pad the level to fixed width
    local padded_level=$(printf "$level_padding" "[$level]")
    
    printf "%s - %s - %s%s\n" "$timestamp" "$padded_level" "$message" "$duration" | tee -a "$LOG_FILE"
}

# Initialize directories with detailed logging
init_directories() {
    echo_log "INFO" "Initializing directory structure"
    
    local dirs=("$LOG_DIR" "$BACKUP_DIR" "$TMP_DIR_TODAY" "$TMP_DIR_OLD")
    
    for dir in "${dirs[@]}"; do
        echo_log "DEBUG" "Checking directory: $dir"
        if powershell.exe -Command "if (!(Test-Path '$dir')) { New-Item -ItemType Directory -Path '$dir' | Out-Null }"; then
            echo_log "INFO" "Directory verified/created: $dir"
        else
            echo_log "ERROR" "Failed to create directory: $dir"
            exit 1
        fi
    done
}

# Enhanced log rotation with detailed tracking
rotate_logs() {
    echo_log "INFO" "Starting log rotation process"
    local found_logs=false
    local processed_files=0
    local current_month_files=0
    local old_files=0
    
    for file in "$LOG_DIR"/application-*.log "$LOG_DIR"/audit-*.log "$LOG_DIR"/localhost.* "$LOG_DIR"/catalina.* "$LOG_DIR"/localhost_access_log.*; do
        [ -e "$file" ] || continue
        ((processed_files++))
        
        LOG_DATE=$(echo "$file" | grep -oE '[0-9]{4}-[0-9]{2}-[0-9]{2}')
        LOG_MONTH=${LOG_DATE:5:2}
        LOG_YEAR=${LOG_DATE:0:4}
        
        if [[ "$LOG_YEAR" == "$CURRENT_YEAR" && "$LOG_MONTH" == "$CURRENT_MONTH" ]]; then
            if mv "$file" "$TMP_DIR_TODAY"/; then
                ((current_month_files++))
            else
                echo_log "ERROR" "Failed to move file: $(basename "$file")"
            fi
        else
            if mv "$file" "$TMP_DIR_OLD"/; then
                ((old_files++))
            else
                echo_log "ERROR" "Failed to move file: $(basename "$file")"
            fi
        fi
        found_logs=true
    done

    echo_log "INFO" "Processed $processed_files files ($current_month_files current month, $old_files old)"
    
    if ! $found_logs; then
        echo_log "WARNING" "No logs found to rotate in $LOG_DIR"
        # Clean up temp directories when no logs found
        rm -rf "$TMP_DIR_TODAY" && echo_log "DEBUG" "Removed empty directory: $TMP_DIR_TODAY"
        rm -rf "$TMP_DIR_OLD" && echo_log "DEBUG" "Removed empty directory: $TMP_DIR_OLD"
        return
    fi

    # Compression with detailed tracking
    echo_log "INFO" "Starting compression process"
    
    ZIP_TODAY_FILE="$BACKUP_DIR/LOGS_${CURRENT_DATE}.zip"
    ZIP_OLD_FILE="$BACKUP_DIR/OLD_LOG_${CURRENT_DATE}.zip"
    
    if [ -n "$(ls -A "$TMP_DIR_TODAY" 2>/dev/null)" ]; then
        echo_log "DEBUG" "Compressing current month logs from $TMP_DIR_TODAY"
        if powershell.exe -Command "Compress-Archive -Path $TMP_DIR_TODAY\* -DestinationPath '$ZIP_TODAY_FILE' -Force "; then
            echo_log "INFO" "Successfully compressed today's logs ($(ls -1 "$TMP_DIR_TODAY" | wc -l) files) into: $ZIP_TODAY_FILE"
            echo_log "DEBUG" "Cleaning up $TMP_DIR_TODAY"
            rm -r "$TMP_DIR_TODAY"/*
            rmdir "$TMP_DIR_TODAY" && echo_log "DEBUG" "Removed directory: $TMP_DIR_TODAY"
        else
            echo_log "ERROR" "Failed to compress today's logs"
        fi
    else
        echo_log "DEBUG" "No current month logs found for compression"
        rmdir "$TMP_DIR_TODAY" 2>/dev/null && echo_log "DEBUG" "Removed empty directory: $TMP_DIR_TODAY"
    fi

    if [ -n "$(ls -A "$TMP_DIR_OLD" 2>/dev/null)" ]; then
        echo_log "DEBUG" "Compressing old logs from $TMP_DIR_OLD"
        if powershell.exe -Command "Compress-Archive -Path $TMP_DIR_OLD\* -DestinationPath '$ZIP_OLD_FILE' -Force "; then
            echo_log "INFO" "Successfully compressed old logs ($(ls -1 "$TMP_DIR_OLD" | wc -l) files) into: $ZIP_OLD_FILE"
            echo_log "DEBUG" "Cleaning up $TMP_DIR_OLD"
            rm -r "$TMP_DIR_OLD"/*
            rmdir "$TMP_DIR_OLD" && echo_log "DEBUG" "Removed directory: $TMP_DIR_OLD"
        else
            echo_log "ERROR" "Failed to compress old logs"
        fi
    else
        echo_log "DEBUG" "No old logs found for compression"
        rmdir "$TMP_DIR_OLD" 2>/dev/null && echo_log "DEBUG" "Removed empty directory: $TMP_DIR_OLD"
    fi
}

transfer_logs() {
    echo_log "INFO" "Starting transfer process to $REMOTE_HOST"
    
    # Ensure remote directory exists
    echo_log "DEBUG" "Creating remote directory $REMOTE_BACKUP_DIR"
    if ssh "$REMOTE_USER@$REMOTE_HOST" "powershell -Command \"New-Item -ItemType Directory -Force -Path '$REMOTE_BACKUP_DIR' | Out-Null\""; then
        echo_log "INFO" "Verified remote directory exists"
    else
        echo_log "ERROR" "Failed to create remote directory"
        return 1
    fi

    local total_files=0
    local success_transfers=0
    local failed_transfers=0
    
    # Count files first
    for zip_file in "$BACKUP_DIR"/*.zip; do
        [ -e "$zip_file" ] && ((total_files++))
    done
    
    echo_log "INFO" "Found $total_files archive(s) to transfer"
    
    for zip_file in "$BACKUP_DIR"/*.zip; do
        [ -e "$zip_file" ] || continue
        
        local zip_filename=$(basename "$zip_file")
        local new_filename="$zip_filename"

        # Check if file already exists on remote server and rename if necessary
        local count=1
        while ssh "$REMOTE_USER@$REMOTE_HOST" "powershell -Command \"Test-Path '$REMOTE_BACKUP_DIR/$new_filename'\"" | grep -iq "True"; do
            new_filename="${zip_filename%.zip}_$count.zip"
            ((count++))
        done

        echo_log "DEBUG" "Final filename for transfer: $new_filename"

        local start_transfer=$(date +%s)
        if scp "$zip_file" "$REMOTE_USER@$REMOTE_HOST:$REMOTE_BACKUP_DIR/$new_filename"; then
            local transfer_time=$(( $(date +%s) - start_transfer ))
            echo_log "INFO" "Successfully transferred $zip_filename as $new_filename in ${transfer_time}s"
            ((success_transfers++))
            
            if rm "$zip_file"; then
                echo_log "DEBUG" "Removed local copy: $zip_filename"
            else
                echo_log "WARNING" "Failed to remove local copy: $zip_filename"
            fi
        else
            echo_log "ERROR" "Failed to transfer $zip_filename"
            ((failed_transfers++))
        fi
    done
    
    echo_log "INFO" "Transfer completed: $success_transfers/$total_files successful, $failed_transfers failed"

    # Remove backup folder if empty
    if [ -d "$BACKUP_DIR" ] && [ -z "$(ls -A "$BACKUP_DIR")" ]; then
        if rmdir "$BACKUP_DIR"; then
            echo_log "INFO" "Removed empty backup directory: $BACKUP_DIR"
        else
            echo_log "WARNING" "Failed to remove backup directory: $BACKUP_DIR"
        fi
    else
        echo_log "DEBUG" "Backup directory not empty or not found: $BACKUP_DIR"
    fi

}

# Main execution with performance tracking
echo_log "INFO" "===== Log Rotation Process Started ====="
echo_log "PERF" "Process started"

init_directories
rotate_logs
transfer_logs

echo_log "PERF" "Process completed"
echo_log "INFO" "===== Log Rotation Process Finished ====="